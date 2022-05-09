package com.team7.project.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project._global.pagination.dto.PaginationResponseDto;
import com.team7.project.interview.dto.InterviewListResponseDto;
import com.team7.project.interview.dto.InterviewResponseDto;
import com.team7.project.interview.dto.InterviewUpdateRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InterviewGeneralService {
    private final InterviewRepository interviewRepository;

    private static final long ONE_HOUR = 1000 * 60 * 60; // 1시간
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public String generatePresignedUrl(String objectKey) {
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + ONE_HOUR);
        System.out.println("expireTime.getTime() = " + expireTime.getTime());

        // Generate the pre-signed URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expireTime);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    public InterviewListResponseDto readAllInterviews(Pageable pageable){
        //      need Refactoring(error handling)
        Page<Interview> interviews = interviewRepository.findAllByIsDone(true, pageable);
        List<InterviewResponseDto.Data> responses = new ArrayList<>();
//        Need Rafactoring. should be move to InterviewResponse Constructor
        for(Interview interview: interviews.getContent()){
            InterviewResponseDto response = new InterviewResponseDto(interview, generatePresignedUrl(interview.getVideoKey()), generatePresignedUrl(interview.getThumbnailKey()));
            responses.add(response.getInterview());
        }

//        must be refactored
        PaginationResponseDto pagination = new PaginationResponseDto((long) pageable.getPageSize(), interviews.getTotalElements(), (long) pageable.getPageNumber() + 1);
        return new InterviewListResponseDto(responses, pagination);
    }

    public InterviewResponseDto readOneInterview(Long interviewId){
        //      need Refactoring(error handling)
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);

        return new InterviewResponseDto(interview, generatePresignedUrl(interview.getVideoKey()), generatePresignedUrl(interview.getThumbnailKey()));
    }

    @Transactional
    public InterviewResponseDto deleteInterview(Long interviewId, Long userId){
        //      need Refactoring(error handling)
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);

//        need Refactoring(error Handling + User integration)
//        if(!interview.getUser().getId().equals(userId))
//        {
//            throw new RuntimeException();
//        }

        InterviewResponseDto response = new InterviewResponseDto(interview, generatePresignedUrl(interview.getVideoKey()), generatePresignedUrl(interview.getThumbnailKey()));

        interviewRepository.deleteById(interviewId);
        return response;
    }

    @Transactional
    public InterviewResponseDto updateInterview(Long interviewId, InterviewUpdateRequestDto requestDto){
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);

//        need Refactoring(error Handling + User integration)
//        if(!interview.getUser().getId().equals(userId))
//        {
//            throw new RuntimeException();
//        }

//        Need Rafactoring. should be move to InterviewResponse Constructor

        interview.update(requestDto.getNote(),requestDto.getIsPublic());

        return new InterviewResponseDto(interview, generatePresignedUrl(interview.getVideoKey()), generatePresignedUrl(interview.getThumbnailKey()));

    }
}
