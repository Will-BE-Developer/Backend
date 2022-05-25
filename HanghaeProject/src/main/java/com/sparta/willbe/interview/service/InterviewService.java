package com.sparta.willbe.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sparta.willbe.batch.tables.WeeklyInterview;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.interview.dto.InterviewListResponseDto;
import com.sparta.willbe.interview.dto.InterviewUpdateRequestDto;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.scrap.repository.ScrapRepository;
import com.sparta.willbe._global.pagination.dto.PaginationResponseDto;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.batch.repository.WeeklyInterviewRepository;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.scrap.model.Scrap;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${cloud.aws.credentials.access-key-upload}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key-upload}")
    private String secretKey;

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
        Long scrapsCount = (long) interview.getScraps().size();
        Long commentsCount = (long) interview.getComments().size();

        String videoPresignedUrl = interview.getIsVideoConverted() ? getPresignedUrl(interview.getVideoKey()) : null;
        String imagePresignedUrl = getThumbnailImageUrl(interview);
        String profilePresignedUrl = getProfileImageUrl(interview.getUser().getProfileImageUrl());

        //5월 2째주 1등 -> 숫자만 추출
        WeeklyInterview Weekly = weeklyInterviewRepository.findByInterviewId(interview.getId());
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
                        .orElseThrow(ErrorMessage.NOT_FOUND_LOGIN_USER::throwError);

        List<InterviewInfoResponseDto.Data> responses = new ArrayList<>();

        Page<Interview> interviews;
        if (sort.equals("스크랩순")) {
            interviews = filter.equals("전체보기") ?
                    interviewRepository.findAllOrderByScrapsCountDesc(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    interviewRepository.findAllByQuestion_CategoryOrderByScrapsCountDesc(CategoryEnum.valueOf(filter), pageable);


        } else {
            interviews = filter.equals("전체보기") ?
                    interviewRepository.findAllByIsDoneAndIsPublic(true, true, pageable) :
                    interviewRepository.findAllByIsDoneAndIsPublicAndQuestion_Category(true, true, CategoryEnum.valueOf(filter), pageable);
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
                        .orElseThrow(ErrorMessage.NOT_FOUND_LOGIN_USER::throwError);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        ErrorMessage.NOT_FOUND_INTERVIEW::throwError
                );


        if (interview.getIsPublic() == false && interview.getUser().getId() != loginUserId) {
            throw ErrorMessage.INVALID_INTERVIEW_VIEW.throwError();
        }

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        return getInterviewResponse(loginUserId, userScrapsId, interview);
    }

    @Transactional
    public InterviewInfoResponseDto updateInterview(Long loginUserId, Long interviewId, InterviewUpdateRequestDto requestDto) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(ErrorMessage.NOT_FOUND_LOGIN_USER::throwError);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ErrorMessage.NOT_FOUND_INTERVIEW::throwError);

        Boolean isMine = Objects.equals(loginUserId, interview.getUser().getId());
        if (isMine == false) {
            throw ErrorMessage.INVALID_INTERVIEW_UPDATE.throwError();
        }

        interview.update(requestDto.getNote(), requestDto.getIsPublic());
        interviewRepository.saveAndFlush(interview);

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        return getInterviewResponse(loginUserId, userScrapsId, interview);
    }

    @Transactional
    public InterviewInfoResponseDto deleteInterview(Long loginUserId, Long interviewId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(ErrorMessage.NOT_FOUND_LOGIN_USER::throwError);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ErrorMessage.NOT_FOUND_INTERVIEW::throwError);

        Boolean isMine = Objects.equals(user.getId(), interview.getUser().getId());
        if (isMine == false) {
            throw ErrorMessage.INVALID_INTERVIEW_DELETE.throwError();
        }

        Set<Long> userScrapsId = getScrapedInterviewIds(user);

        InterviewInfoResponseDto response = getInterviewResponse(loginUserId, userScrapsId, interview);

        //인터뷰 삭제시, 위클리 테이블에 있으면
        //(기존)하위 랭킹 업그레이드 후 삭제 -> (변경)인터뷰 삭제X
        WeeklyInterview itsWeekly = weeklyInterviewRepository.findByInterviewId(interviewId);
        try {
            if (itsWeekly != null) {
                //throw 되었는데 200 으로 리턴됨 -> 클라이언트에게 400전달(추후 수정)
                throw ErrorMessage.UNABLE_DELETE_INTERVIEW_ON_WEEKLY.throwError();
            }
        } catch (Exception e) {
            log.error("{}번 인터뷰는 면접왕이여서 삭제 불가함", interviewId, e); //e안하면 로그 외 익셉션 정보 출력안됨
        }

        //위클리 테이블에 없으면, 인터뷰 삭제(S3에서 영상 삭제, 썸네일은 삭제X)
        //  - 프론트에게 전달하기 위해, video_key컬럼에 "" 저장
        try {
            //S3에서 영상 삭제
            try{
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion(Regions.AP_NORTHEAST_2)
                        .build();
                if (interview.getVideoKey().equals("") == false){
                    amazonFullS3Client.deleteObject(bucket, interview.getVideoKey());
                    //s3Client.deleteObject(bucket, interview.getVideoKey());
                    log.info("S3에서 인터뷰(ID:{}) 영상 삭제 성공(VideoKey:{})", interviewId, interview.getVideoKey());
                }else{
                    log.error("이미 삭제 처리된 인터뷰(ID:{})", interviewId);
                }
            } catch (Exception e) {
                log.error("S3에서 인터뷰(ID:{}) 영상 삭제 에러 - {}", interviewId, e.getMessage());
            }

            //video_key에 "" 저장
            interview.deleteVideoKey();
            response.getInterview().deleteVideoKey();

        } catch (Exception e) {
            log.error("인터뷰 ID {}번 삭제 에러", interviewId, e);
        }

        return response;
    }


}
