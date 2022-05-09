package com.team7.project.interview.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project.advice.RestException;
import com.team7.project.interview.dto.InterviewInfoResponseDto;
import com.team7.project.interview.dto.InterviewPostRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import com.team7.project.user.model.Role;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InterviewPostService {
    private final InterviewGeneralService interviewGeneralService;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

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
    public InterviewInfoResponseDto completeInterview(User user, Long interviewId , InterviewPostRequestDto requestDto) {

        //      need Refactoring(exception handling)
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(RuntimeException::new);
        //      need Refactoring(exception handling)
        Question question = questionRepository.findById(requestDto.getQuestionId())
                .orElseThrow(RuntimeException::new);
        //      need Refactoring(exception handling)

        if(interview.getUser().getId() != user.getId()){
            throw new RestException(HttpStatus.BAD_REQUEST, "현재 사용자는 해당 게시글을 업로드 할 수 없습니다.");
        }

        interview.complete(requestDto.getNote(), requestDto.getIsPublic(), user, question);

        user.getInterviews().add(interview);

        return new InterviewInfoResponseDto(interview, interviewGeneralService.generatePresignedUrl(interview.getVideoKey()), interviewGeneralService.generatePresignedUrl(interview.getThumbnailKey()),true);
    }

}
