package com.sparta.willbe.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe._global.pagination.dto.PaginationResponseDto;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.interview.dto.InterviewListResponseDto;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InterviewMyPageService {
    private final InterviewGeneralService interviewGeneralService;
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

    public InterviewListResponseDto readAllMyInterviews(Pageable pageable, Long loginUserId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(ErrorMessage.NOT_FOUND_LOGIN_USER::throwError);

        Page<Interview> interviews = interviewRepository.findAllByIsDoneAndUser_Id(true, loginUserId, pageable);

        List<InterviewInfoResponseDto.Data> responses = new ArrayList<>();

        Set<Long> userScrapsId = interviewGeneralService.createUserScrapIds(user);

        for (Interview interview : interviews.getContent()) {

            InterviewInfoResponseDto response = interviewGeneralService.createInterviewResponse(loginUserId, userScrapsId, interview);

            responses.add(response.getInterview());

        }

        PaginationResponseDto pagination = new PaginationResponseDto((long) pageable.getPageSize(),
                interviews.getTotalElements(),
                (long) pageable.getPageNumber() + 1);

        return new InterviewListResponseDto(responses, pagination);
    }

    public InterviewListResponseDto readAllMyScraps(Pageable pageable, Long loginUserId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(ErrorMessage.UNAUTHORIZED_USER::throwError);

        Page<Interview> interviews = interviewRepository.findAllByIsDoneAndScraps_User_Id(true, loginUserId, pageable);

        List<InterviewInfoResponseDto.Data> responses = new ArrayList<>();

        Set<Long> userScrapsId = interviewGeneralService.createUserScrapIds(user);

        for (Interview interview : interviews.getContent()) {

            InterviewInfoResponseDto response = interviewGeneralService.createInterviewResponse(loginUserId, userScrapsId, interview);

            responses.add(response.getInterview());

        }

        PaginationResponseDto pagination = new PaginationResponseDto((long) pageable.getPageSize(),
                interviews.getTotalElements(),
                (long) pageable.getPageNumber() + 1);

        return new InterviewListResponseDto(responses, pagination);
    }


}
