package com.team7.project.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project._global.pagination.dto.PaginationResponseDto;
import com.team7.project.advice.ErrorMessage;
import com.team7.project.batch.BATCH_repository.BATCH_WeeklyInterviewRepository;
import com.team7.project.batch.tables.BATCH_WeeklyInterview;
import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.dto.InterviewInfoResponseDto;
import com.team7.project.interview.dto.InterviewListResponseDto;
import com.team7.project.interview.dto.InterviewUpdateRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.scrap.model.Scrap;
import com.team7.project.scrap.repository.ScrapRepository;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
@Service
//@Transactional(readOnly = true)
@Transactional
public class InterviewGeneralService {
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final BATCH_WeeklyInterviewRepository weeklyInterviewRepository;
    private final ScrapRepository scrapRepository;

    private static final long ONE_HOUR = 1000 * 60 * 60; // 1시간
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public String generatePresignedUrl(String objectKey) {

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

    public String generateProfileImageUrl(String image) {
        if (image == null) {
            return null;
        }
        return (image.contains("http://") | image.contains("https://")) ? image : generatePresignedUrl(image);
    }

    public Set<Long> createUserScrapIds(User user) {
        Set<Long> userScrapsId = new HashSet<>();
        if (user != null) {
            for (Scrap scrap : user.getScraps()) {
                userScrapsId.add(scrap.getInterview().getId());
            }
        }
        return userScrapsId;
    }

    public InterviewInfoResponseDto createInterviewResponse(Long loginUserId, Set<Long> userScrapsId, Interview interview) {
        Boolean isMine = loginUserId == null ? null : Objects.equals(interview.getUser().getId(), loginUserId);
        System.out.println("interview.getIsVideoConverted() = " + interview.getIsVideoConverted());

        Boolean scrapsMe = loginUserId == null ? null : userScrapsId.contains(interview.getId());
        Long scrapsCount = (long) interview.getScraps().size();
        Long commentsCount = (long) interview.getComments().size();

        String videoPresignedUrl = interview.getIsVideoConverted() ? generatePresignedUrl(interview.getVideoKey()) : null;
        String imagePresignedUrl = generatePresignedUrl(interview.getThumbnailKey());
        String profilePresignedUrl = generateProfileImageUrl(interview.getUser().getProfileImageUrl());

        return new InterviewInfoResponseDto(interview, videoPresignedUrl, imagePresignedUrl, profilePresignedUrl, isMine, scrapsMe, scrapsCount, commentsCount);
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

        Set<Long> userScrapsId = createUserScrapIds(user);

        for (Interview interview : interviews.getContent()) {

            InterviewInfoResponseDto response = createInterviewResponse(loginUserId, userScrapsId, interview);

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


        if (interview.getIsPublic() == false & interview.getUser().getId() != loginUserId) {
            throw ErrorMessage.INVALID_INTERVIEW_VIEW.throwError();
        }

        Set<Long> userScrapsId = createUserScrapIds(user);

        return createInterviewResponse(loginUserId, userScrapsId, interview);
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

        Set<Long> userScrapsId = createUserScrapIds(user);

        return createInterviewResponse(loginUserId, userScrapsId, interview);
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

        Set<Long> userScrapsId = createUserScrapIds(user);

        InterviewInfoResponseDto response = createInterviewResponse(loginUserId, userScrapsId, interview);

        //인터뷰 삭제전 면접왕 뱃지가 있으면, 밑에 등수 수정
        if (interview.getBadge().equals("NONE") == false){
            try{
                String badge = interview.getBadge();
                int ranking = Integer.parseInt(badge.substring(8, 9));

                int[] totalRank = {1, 2, 3, 4, 5};
                int[] lowerRankArray = Arrays.copyOfRange(totalRank, ranking, totalRank.length);
                //하위 랭킹 for문, 뱃지 수정
                for(int num: lowerRankArray){
                    String lowerRank = badge.substring(0, 8) + num + "등";
                    BATCH_WeeklyInterview weekly = weeklyInterviewRepository.findByWeeklyBadge(lowerRank);

                    String newRank = badge.substring(0, 8) + (num-1) + "등";
                    System.out.println("기존 랭킹: " + lowerRank + ", 수정된 랭킹: " + newRank);

                    //위클리 테이블 랭링 수정
                    weekly.setWeeklyBadge(newRank);
                    weekly.setBadge((num-1) + "등");
                    weeklyInterviewRepository.save(weekly);

                    //인터뷰 테이블 랭킹 수정
                    Interview lowInterview = weekly.getInterview();
                    lowInterview.updateBadge(newRank);
                    interviewRepository.save(lowInterview);
                }
                //스크랩, 인터뷰 삭제(위클리도 삭제됨)
                scrapRepository.deleteByInterviewId(interviewId);
                interview.makeScrapNullForDelete();
                interviewRepository.deleteById(interviewId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return response;
    }


}
