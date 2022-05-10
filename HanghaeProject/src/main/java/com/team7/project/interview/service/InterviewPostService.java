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

        // Generate the pre-signed URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expireTime);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }


    @Transactional
    public Interview createInterviewDraft(User user) {
        Long userId = user.getId();
        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));
        String objectKey = userId + "-"+ suffix;

        Interview interview = interviewRepository.save(new Interview("videos/" + objectKey, "thumbnails/" + objectKey, user));
        User userSet = userRepository.findById(userId).orElseThrow(
                () -> new RestException(HttpStatus.BAD_REQUEST,"해당 유저가 존재하지 않습니다.")
        );
        userSet.getInterviews().add(interview);

        return interview;
    }

    @Transactional
    public InterviewInfoResponseDto completeInterview(User user, Long interviewId , InterviewPostRequestDto requestDto) {
        
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST,"해당 인터뷰가 존재하지 않습니다.")
                );

        Question question = questionRepository.findById(requestDto.getQuestionId())
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST,"해당 질문이 존재하지 않습니다.")
                );

        if(interview.getUser().getId() != user.getId()){
            throw new RestException(HttpStatus.BAD_REQUEST, "현재 사용자는 해당 게시글을 업로드 할 수 없습니다.");
        }

        interview.complete(requestDto.getNote(), requestDto.getIsPublic(), question);

        return new InterviewInfoResponseDto(interview, interviewGeneralService.generatePresignedUrl(interview.getVideoKey()), interviewGeneralService.generatePresignedUrl(interview.getThumbnailKey()),true);
    }

}
