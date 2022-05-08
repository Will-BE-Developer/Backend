package com.team7.project.interview.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project.interview.dto.InterviewResponse;
import com.team7.project.interview.dto.InterviewUploadRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.question.dto.QuestionRequestDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import com.team7.project.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InterviewUploadService {
    private final InterviewGeneralService interviewGeneralService;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;

    private static final long ONE_HOUR = 1000 * 60 * 60; // 1시간
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public String generatePresignedPost(String objectKey) {
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + ONE_HOUR);
        System.out.println("expireTime.getTime() = " + expireTime.getTime());

        // Generate the pre-signed URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expireTime);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }


    @Transactional
    public Interview createInterviewDraft(Long userId) {
        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));
        String objectKey = userId + "-"+ suffix;

        return interviewRepository.save(new Interview("videos/" + objectKey, "thumbnails/" + objectKey));
    }

    @Transactional
    public InterviewResponse completeInterview(Long userId, Long interviewId ,InterviewUploadRequestDto requestDto) {

//      need Refactoring
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);
        //      need Refactoring
        Question question = questionRepository.findById(requestDto.getQuestionId())
                .orElseThrow(RuntimeException::new);
        //      need Refactoring
        User user = new User();

        interview.complete(requestDto.getNote(),requestDto.getIsPublic(), user, question);

//        need Refactoring****
//        user.getInterviews().add(interview);

        return new InterviewResponse(interview, interviewGeneralService.generatePresignedUrl(interview.getVideoKey()), interviewGeneralService.generatePresignedUrl(interview.getThumbnailKey()));
    }

}
