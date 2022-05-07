package com.team7.project.interview.controller;

import com.team7.project.interview.dto.InterviewDraftResponse;
import com.team7.project.interview.dto.InterviewResponse;
import com.team7.project.interview.dto.InterviewUpdateRequestDto;
import com.team7.project.interview.dto.InterviewUploadRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.service.InterviewGeneralService;
import com.team7.project.interview.service.InterviewUploadService;
import com.team7.project.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
public class InterviewController {
    private final InterviewUploadService interviewUploadService;
    private final InterviewGeneralService interviewGeneralService;

    @GetMapping("/api/interviews/draft")
    public InterviewDraftResponse createInterviewDraft() {
//       assume user id is 1
        Interview interview = interviewUploadService.createInterviewDraft(1L);

        String videoUrl = interviewUploadService.generatePresignedPost(interview.getVideoKey());
        String thumbnailUrl = interviewUploadService.generatePresignedPost(interview.getThumbnailKey());

        return new InterviewDraftResponse(new InterviewDraftResponse.InterviewDraftBody(interview.getId()),
                new InterviewDraftResponse.UrlBody(videoUrl, thumbnailUrl));
    }

    @PostMapping("/api/interviews/{interviewId}")
    public InterviewResponse completeInterview(@PathVariable Long interviewId, InterviewUploadRequestDto requestDto) {
//       assume user id is 1, nickname is TestNickname
        Interview interview = interviewUploadService.completeInterview(1L, interviewId, requestDto);

//        Need Rafactoring. should be move to InterviewResponse Constructor
        return InterviewResponse.builder()
                .id(interview.getId())
//                This is problem
                .user(new InterviewResponse.UesrBody(1L, "TestNickName","testGit","testprofileUrl","testIntroduce"))
                .video(interviewGeneralService.generatePresignedUrl(interview.getVideoKey()))
                .thumbnail(interviewGeneralService.generatePresignedUrl(interview.getThumbnailKey()))
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
    }

    @PutMapping("/api/interviews/{interviewId}")
    public InterviewResponse update(@PathVariable Long interviewId, InterviewUpdateRequestDto requestDto) {
//       assume user id is 1, nickname is TestNickname
        return interviewGeneralService.updateInterview(interviewId, requestDto);
    }

    @DeleteMapping("/api/interviews/{interviewId}")
    public InterviewResponse deleteInterview(@PathVariable Long interviewId) {
//       assume user id is 1, nickname is TestNickname
        return interviewGeneralService.deleteInterview(interviewId, 1L);
    }





}
