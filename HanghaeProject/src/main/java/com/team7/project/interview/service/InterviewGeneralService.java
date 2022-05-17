package com.team7.project.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

    @Value("${cloud.aws.credentials.access-key-upload}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key-upload}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

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

    public String generateThumbnailImageUrl(Interview interview) {
        if (interview.getIsThumbnailConverted()) {
            return generatePresignedUrl(interview.getThumbnailKey());
        } else {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            try {
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion(region)
                        .build();
                boolean doesItExists = s3Client.doesObjectExist(bucket, interview.getThumbnailKey());
                if (doesItExists) {
                    interview.convertThumbnail();
                    return generatePresignedUrl(interview.getThumbnailKey());
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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

    @Transactional
    public void initInterview(){
        List<Interview> interviews = interviewRepository.findAll();
        for(Interview interview1 : interviews){
            interview1.convertThumbnail();
        }
    }

    public InterviewInfoResponseDto createInterviewResponse(Long loginUserId, Set<Long> userScrapsId, Interview interview) {
//        initInterview();
        Boolean isMine = loginUserId == null ? null : Objects.equals(interview.getUser().getId(), loginUserId);
        Boolean scrapsMe = loginUserId == null ? null : userScrapsId.contains(interview.getId());
        Long scrapsCount = (long) interview.getScraps().size();
        Long commentsCount = (long) interview.getComments().size();


        String videoPresignedUrl = interview.getIsVideoConverted() ? generatePresignedUrl(interview.getVideoKey()) : null;
        String imagePresignedUrl = generateThumbnailImageUrl(interview);
        String profilePresignedUrl = generateProfileImageUrl(interview.getUser().getProfileImageUrl());

        //5월 2째주 1등 -> 숫자만 추출
        BATCH_WeeklyInterview Weekly = weeklyInterviewRepository.findByInterviewId(interview.getId());
        boolean itsWeekly = Weekly == null ? false : true;
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

        scrapRepository.deleteByInterviewId(interviewId);

        //인터뷰 삭제전 면접왕 뱃지(Gold,Silver,Bronze)가 있으면, 밑에 등수 수정
        if (interview.getBadge().equals("NONE") == false) {
            try {
                //인터뷰의 위클리 row 검색해서, 위클리뱃지를 가져오고
                BATCH_WeeklyInterview itsWeekly = weeklyInterviewRepository.findByInterviewId(interviewId);
                String weeklyBadge = itsWeekly.getWeeklyBadge(); //5월 2째주 1등
                String itsBadge = interview.getBadge(); //Gold
                int ranking = Integer.parseInt(weeklyBadge.substring(7, 8));

                String[] badge = {"Gold", "Silver", "Bronze"};
                int[] totalRank = {1, 2, 3, 4, 5};
                //전체 랭킹을 현재 면접왕 숫자만큼만
                int nowTotalRank = (int) weeklyInterviewRepository.count();
                totalRank = Arrays.copyOf(totalRank, nowTotalRank);

                //전체 5등 중에 하위 등수 애들만 배열
                int[] lowerRankArray = Arrays.copyOfRange(totalRank, ranking, totalRank.length);
                //하위 랭킹 for문, 뱃지 수정
                for (int lowerRanking : lowerRankArray) { //면접왕이 4등까지만 있을때, 5등이 없어서 에러남
                    //기존 위클리 등수 정보로 위클리 row 뽑아와서
                    String lowerWeeklyRank = weeklyBadge.substring(0, 7) + lowerRanking + "등";
                    BATCH_WeeklyInterview weekly = weeklyInterviewRepository.findByWeeklyBadge(lowerWeeklyRank);
                    //새로운 등수 부여
                    int lowerNewRanking = lowerRanking - 1;
                    String newWeeklyRank = weeklyBadge.substring(0, 7) + (lowerRanking - 1) + "등";
                    System.out.println("기존 랭킹: " + lowerWeeklyRank + ", 수정된 랭킹: " + newWeeklyRank);

                    //위클리 테이블 랭링 수정 저장
                    weekly.setWeeklyBadge(newWeeklyRank);
                    if (lowerNewRanking <= 3){
                        weekly.setBadge(badge[lowerNewRanking-1]);
                    }else{
                        weekly.setBadge("NONE");
                    }
                    weeklyInterviewRepository.save(weekly);

                    //인터뷰 테이블 뱃지 수정 저장
                    Interview lowInterview = weekly.getInterview();
                    if (lowerNewRanking <= 3){
                        lowInterview.updateBadge(badge[lowerNewRanking-1]);
                    }else{
                        lowInterview.updateBadge("NONE");
                    }
                    interviewRepository.save(lowInterview);
                }

                //스크랩, 인터뷰 삭제(위클리도 삭제됨)
                //scrapRepository.deleteByInterviewId(interviewId);
                //interview.makeScrapNullForDelete();
                //interviewRepository.deleteById(interviewId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        interviewRepository.deleteById(interviewId);
        return response;
    }


}
