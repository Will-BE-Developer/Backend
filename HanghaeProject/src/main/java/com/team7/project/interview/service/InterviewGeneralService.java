package com.team7.project.interview.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project.interview.dto.InterviewResponse;
import com.team7.project.interview.dto.InterviewUpdateRequestDto;
import com.team7.project.interview.dto.InterviewUploadRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        LocalDateTime now = LocalDateTime.now();

        // Generate the pre-signed URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(java.sql.Timestamp.valueOf(now.plusNanos(ONE_HOUR)));

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    @Transactional
    public InterviewResponse deleteInterview(Long interviewId, Long userId){
        //      need Refactoring(error handling)
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);

//        need Refactoring(error Handling + User integration)
//        if(!interview.getUser().getId().equals(userId))
//        {
//            throw new RuntimeException();
//        }

//        Need Rafactoring. should be move to InterviewResponse Constructor
        InterviewResponse response = InterviewResponse.builder()
                .id(interview.getId())
                .user(new InterviewResponse.UesrBody(1L, "TestNickName","testGit","testprofileUrl","testIntroduce"))
                .video(generatePresignedUrl(interview.getVideoKey()))
                .thumbnail(generatePresignedUrl(interview.getThumbnailKey()))
                .question(interview.getQuestion().getContents())
                .badge(interview.getBadge())
                .note(interview.getMemo())
                .scrapsMe(true)
                .scrapsCount(0L)
                .likesCount(0L)
                .isPublic(interview.getIsPublic())
                .createdAt(interview.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(interview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        interviewRepository.deleteById(interviewId);
        return response;
    }

    @Transactional
    public InterviewResponse updateInterview(Long interviewId, InterviewUpdateRequestDto requestDto){
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);

//        need Refactoring(error Handling + User integration)
//        if(!interview.getUser().getId().equals(userId))
//        {
//            throw new RuntimeException();
//        }

//        Need Rafactoring. should be move to InterviewResponse Constructor

        interview.update(requestDto.getNote(),requestDto.getIsPublic());
        InterviewResponse response = InterviewResponse.builder()
                .id(interview.getId())
                .user(new InterviewResponse.UesrBody(1L, "TestNickName","testGit","testprofileUrl","testIntroduce"))
                .video(generatePresignedUrl(interview.getVideoKey()))
                .thumbnail(generatePresignedUrl(interview.getThumbnailKey()))
                .question(interview.getQuestion().getContents())
                .badge(interview.getBadge())
                .note(interview.getMemo())
                .scrapsMe(true)
                .scrapsCount(0L)
                .likesCount(0L)
                .isPublic(interview.getIsPublic())
                .createdAt(interview.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(interview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        return response;

    }
}
