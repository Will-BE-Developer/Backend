package com.sparta.willbe.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sparta.willbe.batch.tables.WeeklyInterview;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.interview.dto.InterviewListResponseDto;
import com.sparta.willbe.interview.dto.InterviewUpdateRequestDto;
import com.sparta.willbe.interview.exception.InterviewForbiddenDeleteException;
import com.sparta.willbe.interview.exception.InterviewForbiddenGetException;
import com.sparta.willbe.interview.exception.InterviewForbiddenUpdateException;
import com.sparta.willbe.interview.exception.InterviewNotFoundException;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.scrap.repository.ScrapRepository;
import com.sparta.willbe._global.pagination.dto.PaginationResponseDto;
import com.sparta.willbe.batch.repository.WeeklyInterviewRepository;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.scrap.model.Scrap;
import com.sparta.willbe.user.exception.UserNotFoundException;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final WeeklyInterviewRepository weeklyInterviewRepository;
    private final ScrapRepository scrapRepository;

    private static final long ONE_HOUR = 1000 * 60 * 60; //1시간

    private final AmazonS3Client amazonS3Client;

    private final AmazonS3Client amazonFullS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String getPresignedUrl(String objectKey) {

        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + ONE_HOUR);

        // Generate the pre-signed URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expireTime);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    public String getProfileImageUrl(String image) {
        if (image == null) {
            return null;
        }
        return (image.contains("http://") | image.contains("https://")) ? image : getPresignedUrl(image);
    }

    public String getThumbnailImageUrl(Interview interview) {
        if (interview.getIsThumbnailConverted()) {
            return getPresignedUrl(interview.getThumbnailKey());
        } else {
            try {
                boolean doesItExists = amazonFullS3Client.doesObjectExist(bucket, interview.getThumbnailKey());
                if (doesItExists) {
                    interview.convertThumbnail();
                    return getPresignedUrl(interview.getThumbnailKey());
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Set<Long> getScrapedInterviewIds(User user) {
        Set<Long> userScrapsId = new HashSet<>();
        if (user != null) {
            for (Scrap scrap : user.getScraps()) {
                userScrapsId.add(scrap.getInterview().getId());
            }
        }
        return userScrapsId;
    }

    public InterviewInfoResponseDto getInterviewResponse(Long loginUserId, Set<Long> userScrapsId, Interview interview) {
        Boolean isMine = loginUserId == null ? null : Objects.equals(interview.getUser().getId(), loginUserId);
        Boolean scrapsMe = loginUserId == null ? null : userScrapsId.contains(interview.getId());
        //n+1 - complete
        Long scrapsCount = (long) interview.getScraps().size();
        //n+1 - complete
        Long commentsCount = (long) interview.getComments().size();

        String videoPresignedUrl = interview.getIsVideoConverted() ? getPresignedUrl(interview.getVideoKey()) : null;
        String imagePresignedUrl = getThumbnailImageUrl(interview);
        //n+1 - complete
        String profilePresignedUrl = getProfileImageUrl(interview.getUser().getProfileImageUrl());

        //5월 2째주 1등 -> 숫자만 추출
        // -> query did not return a unique result -> 최신꺼 1개만
        //WeeklyInterview Weekly = weeklyInterviewRepository.findByInterviewId(interview.getId());
        WeeklyInterview Weekly = weeklyInterviewRepository.findTopByInterviewIdOrderByIdDesc(interview.getId());
        if( Weekly != null){
            log.info("Weekly Interview 최신 top 1: {}", Weekly.getId());
        }
        boolean itsWeekly = Weekly != null;
        int month = itsWeekly ? Integer.parseInt(Weekly.getWeeklyBadge().substring(0, 1)) : -1;
        int week = itsWeekly ? Integer.parseInt(Weekly.getWeeklyBadge().substring(3, 4)) : -1;
        int ranking = itsWeekly ? Integer.parseInt(Weekly.getWeeklyBadge().substring(7, 8)) : -1;

        return new InterviewInfoResponseDto(interview, videoPresignedUrl, imagePresignedUrl, profilePresignedUrl,
                isMine, scrapsMe, scrapsCount, commentsCount,
                month, week, ranking);
    }

    public InterviewListResponseDto readAllInterviews(Long loginUserId, String sort, String filter, Pageable pageable) {

        User user = loginUserId == null ?
                null :
                userRepository.findById(loginUserId)
                        .orElseThrow(UserNotFoundException::new);

        List<InterviewInfoResponseDto.Data> responses = new ArrayList<>();

        Page<Interview> interviews;
        if (sort.equals("스크랩순")) {
            interviews = filter.equals("전체보기") ?
                    interviewRepository.findAllOrderByScrapsCountDesc(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    interviewRepository.findAllByQuestion_CategoryOrderByScrapsCountDesc(CategoryEnum.valueOf(filter), pageable);


        } else {
            interviews = filter.equals("전체보기") ?
                    interviewRepository.findAllByIsDoneTrueAndIsPublicTrueAndUser_IsDeletedFalse(pageable) :
                    interviewRepository.findAllByIsDoneTrueAndIsPublicTrueAndUser_IsDeletedFalseAndQuestion_Category(CategoryEnum.valueOf(filter), pageable);
        }

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        for (Interview interview : interviews.getContent()) {

            InterviewInfoResponseDto response = getInterviewResponse(loginUserId, userScrapsId, interview);

            responses.add(response.getInterview());

        }

        PaginationResponseDto pagination = new PaginationResponseDto((long) pageable.getPageSize(),
                interviews.getTotalElements(),
                (long) pageable.getPageNumber() + 1);

        return new InterviewListResponseDto(responses, pagination);
    }

    public InterviewInfoResponseDto readOneInterview(Long interviewId, Long loginUserId) {

        User user = loginUserId == null ?
                null :
                userRepository.findById(loginUserId)
                        .orElseThrow(UserNotFoundException::new);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        InterviewNotFoundException::new
                );


        if (interview.getIsPublic() == false && interview.getUser().getId() != loginUserId) {
            throw new InterviewForbiddenGetException();
        }

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        return getInterviewResponse(loginUserId, userScrapsId, interview);
    }

    @Transactional
    public InterviewInfoResponseDto updateInterview(Long loginUserId, Long interviewId, InterviewUpdateRequestDto requestDto) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(UserNotFoundException::new);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewNotFoundException::new);

        Boolean isMine = Objects.equals(loginUserId, interview.getUser().getId());
        if (isMine == false) {
            throw new InterviewForbiddenUpdateException();
        }

        interview.update(requestDto.getNote(), requestDto.getIsPublic());
        interviewRepository.saveAndFlush(interview);

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        return getInterviewResponse(loginUserId, userScrapsId, interview);
    }

    @Transactional
    public InterviewInfoResponseDto deleteInterview(Long loginUserId, Long interviewId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(UserNotFoundException::new);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewNotFoundException::new);

        Boolean isMine = Objects.equals(loginUserId, interview.getUser().getId());
        if (isMine == false) {
            throw new InterviewForbiddenDeleteException();
        }

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        InterviewInfoResponseDto response = getInterviewResponse(loginUserId, userScrapsId, interview);

        //인터뷰 삭제시 -> (변경)인터뷰 삭제, 위클리테이블은 유지
        //S3에서 영상 삭제
        try{
            amazonFullS3Client.deleteObject(bucket, interview.getVideoKey());
            log.info("S3에서 인터뷰(ID:{}) 영상 삭제 성공(VideoKey:{})", interviewId, interview.getVideoKey());

        } catch (Exception e) {
            log.error("S3에서 인터뷰(ID:{}) 영상 삭제 에러 - {}", interviewId, e.getMessage());
            Sentry.captureException(e);
        }

        scrapRepository.deleteByInterviewId(interviewId);
        interview.makeScrapNullForDelete();
        interviewRepository.deleteById(interviewId);

        return response;
    }


}
