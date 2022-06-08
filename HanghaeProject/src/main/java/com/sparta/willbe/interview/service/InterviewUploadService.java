package com.sparta.willbe.interview.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.interview.dto.InterviewPostRequestDto;
import com.sparta.willbe.interview.exception.DraftNotFoundException;
import com.sparta.willbe.interview.exception.InterviewForbiddenPostException;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.question.exception.QuestionNotFoundException;
import com.sparta.willbe.question.model.Question;
import com.sparta.willbe.question.repostitory.QuestionRepository;
import com.sparta.willbe.user.exception.UserNotFoundException;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
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
public class InterviewUploadService {
    private final InterviewService interviewService;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final InterviewConvertService interviewConvertService;

    private static final long ONE_HOUR = 1000 * 60 * 60; // 1시간
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public String getPresignedPost(String objectKey) {
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
                UserNotFoundException::new
        );

        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS"));
        String objectKey = loginUserId + "-" + suffix;

        Interview interview = interviewRepository.save(new Interview("videos/" + objectKey+".webm", "thumbnails/" + objectKey +".png", user));

        user.getInterviews().add(interview);

        return interview;
    }

    @Transactional
    public InterviewInfoResponseDto completeInterview(Long loginUserId, Long interviewId, InterviewPostRequestDto requestDto) throws IOException {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(UserNotFoundException::new);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(DraftNotFoundException::new);

        Question question = questionRepository.findById(requestDto.getQuestionId())
                .orElseThrow(QuestionNotFoundException::new);


        if (interview.getUser().getId() != user.getId()) {
            throw new InterviewForbiddenPostException();
        }

        String convertedObjectkey = interviewConvertService.webmToMp4(interview.getVideoKey(), interview.getId());
        log.info(interview.getVideoKey() + " To " + convertedObjectkey);

        interview.complete(requestDto.getNote(),
                requestDto.getIsPublic(),
                question,
                interview.getVideoKey().replace(".webm",".mp4"),
                "re" + interview.getThumbnailKey());

        return new InterviewInfoResponseDto(interview,
                interviewService.getPresignedUrl(interview.getVideoKey()),
                interviewService.getThumbnailImageUrl(interview),
                interviewService.getProfileImageUrl(interview.getUser().getProfileImageUrl()),
                true, false, 0L, 0L);
    }

//    @Transactional
//    public void dummyInterview(int seq) {
//        User user = userRepository.findById(216L).orElseThrow(
//                UserNotFoundException::new
//        );
//
//        Question question = questionRepository.findById((long)(Math.random()*(100)+202))
//                .orElseThrow(QuestionNotFoundException::new);
//
//        String objectKey = "dummy" + seq;
//
//
//        Interview interview = new Interview("videos/" + objectKey+".webm", "thumbnails/" + objectKey +".png", user);
//        interview.complete("DummyNote",
//                true,
//                question,
//                interview.getVideoKey().replace(".webm",".mp4"),
//                "re" + interview.getThumbnailKey());
//        interview.convertVideo();
//
//        interviewRepository.save(interview);
//
//        user.getInterviews().add(interview);
//
//    }


}
