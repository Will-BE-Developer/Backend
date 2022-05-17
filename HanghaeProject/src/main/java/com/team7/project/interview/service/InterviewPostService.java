package com.team7.project.interview.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.team7.project.advice.ErrorMessage;
import com.team7.project.interview.dto.InterviewInfoResponseDto;
import com.team7.project.interview.dto.InterviewPostRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InterviewPostService {
    private final InterviewGeneralService interviewGeneralService;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final InterviewConvertService interviewConvertService;

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
    public Interview createInterviewDraft(Long loginUserId) {

        User user = userRepository.findById(loginUserId).orElseThrow(
                ErrorMessage.NOT_FOUND_LOGIN_USER::throwError
        );

        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));
        String objectKey = loginUserId + "-" + suffix;

        Interview interview = interviewRepository.save(new Interview("videos/" + objectKey+".webm", "thumbnails/" + objectKey +".png", user));

        user.getInterviews().add(interview);

        return interview;
    }

    @Transactional
    public InterviewInfoResponseDto completeInterview(Long loginUserId, Long interviewId, InterviewPostRequestDto requestDto) throws IOException {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ErrorMessage.NOT_FOUND_DRAFT::throwError);

        Question question = questionRepository.findById(requestDto.getQuestionId())
                .orElseThrow(ErrorMessage.NOT_FOUND_QUESTION::throwError);


        if (interview.getUser().getId() != loginUserId) {
            throw ErrorMessage.INVALID_INTERVIEW_POST.throwError();
        }

        String convertedObjectkey = interviewConvertService.webmToMp4(interview.getVideoKey(), interview.getId());
        log.info(interview.getVideoKey() + " To " + convertedObjectkey);

        interview.complete(requestDto.getNote(),
                requestDto.getIsPublic(),
                question,
                interview.getVideoKey().replace(".webm",".mp4"),
                "re" + interview.getThumbnailKey());

        return new InterviewInfoResponseDto(interview,
                interviewGeneralService.generatePresignedUrl(interview.getVideoKey()),
                interviewGeneralService.generatePresignedUrl(interview.getThumbnailKey()),
                interviewGeneralService.generateProfileImageUrl(interview.getUser().getProfileImageUrl()),
                true, false, 0L, 0L);
    }

}
