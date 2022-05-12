package com.team7.project.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project._global.pagination.dto.PaginationResponseDto;
import com.team7.project.advice.RestException;
import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.dto.InterviewListResponseDto;
import com.team7.project.interview.dto.InterviewInfoResponseDto;
import com.team7.project.interview.dto.InterviewUpdateRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.scrap.model.Scrap;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InterviewGeneralService {
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

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
        if(image == null){
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

        Boolean scrapsMe = loginUserId == null ? null : userScrapsId.contains(interview.getId());
        Long scrapsCount = (long) interview.getScraps().size();

        String videoPresignedUrl = generatePresignedUrl(interview.getVideoKey());
        String imagePresignedUrl = generatePresignedUrl(interview.getThumbnailKey());
        String profilePresignedUrl = generateProfileImageUrl(interview.getUser().getProfileImageUrl());

        return new InterviewInfoResponseDto(interview, videoPresignedUrl, imagePresignedUrl,profilePresignedUrl, isMine, scrapsMe, scrapsCount);
    }


    public InterviewListResponseDto readAllInterviews(Long loginUserId, String sort, String filter, Pageable pageable) {

        User user = loginUserId == null ?
                null :
                userRepository.findById(loginUserId)
                        .orElseThrow(
                                () -> new RestException(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다.")
                        );

        List<InterviewInfoResponseDto.Data> responses = new ArrayList<>();

        Page<Interview> interviews;
        if (sort.equals("scrap")) {
            interviews = filter.equals("default") ?
                    interviewRepository.findAllOrderByScrapsCountDesc(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())) :
                    interviewRepository.findAllByQuestion_CategoryOrderByScrapsCountDesc(CategoryEnum.valueOf(filter), pageable);


        } else {
            interviews = filter.equals("default") ?
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
                        .orElseThrow(
                                () -> new RestException(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다.")
                        );

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 인터뷰가 존재하지 않습니다.")
                );

        Set<Long> userScrapsId = createUserScrapIds(user);

        return createInterviewResponse(loginUserId, userScrapsId, interview);
    }

    @Transactional
    public InterviewInfoResponseDto updateInterview(Long loginUserId, Long interviewId, InterviewUpdateRequestDto requestDto) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다.")
                );

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 인터뷰가 존재하지 않습니다.")
                );

        Boolean isMine = Objects.equals(loginUserId, interview.getUser().getId());
        if (isMine == false) {
            throw new RestException(HttpStatus.BAD_REQUEST, "현재 사용자는 해당 인터뷰를 수정 할 수 않습니다.");
        }

        interview.update(requestDto.getNote(), requestDto.getIsPublic());
        interviewRepository.saveAndFlush(interview);

        Set<Long> userScrapsId = createUserScrapIds(user);

        return createInterviewResponse(loginUserId, userScrapsId, interview);
    }

    @Transactional
    public InterviewInfoResponseDto deleteInterview(Long loginUserId, Long interviewId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다.")
                );

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 인터뷰가 존재하지 않습니다.")
                );

        Boolean isMine = Objects.equals(user.getId(), interview.getUser().getId());
        if (isMine == false) {
            throw new RestException(HttpStatus.BAD_REQUEST, "현재 사용자는 해당 인터뷰를 수정 할 수 없습니다.");
        }

        Set<Long> userScrapsId = createUserScrapIds(user);

        InterviewInfoResponseDto response = createInterviewResponse(loginUserId, userScrapsId, interview);

        interviewRepository.deleteById(interviewId);

        return response;
    }

}
