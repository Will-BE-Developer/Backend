package com.team7.project.interview.controller;

import com.team7.project.interview.dto.*;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.service.InterviewGeneralService;
import com.team7.project.interview.service.InterviewUploadService;
import com.team7.project.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
public class InterviewController {
    private final InterviewUploadService interviewUploadService;
    private final InterviewGeneralService interviewGeneralService;


    @GetMapping("/api/interviews")
    public ResponseEntity<InterviewListResponse> readInterviews(@RequestParam(value = "per", defaultValue = "8") int per, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "sort", defaultValue = "new") String sort) {
//      assume user id is 1
        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());
        InterviewListResponse body = interviewGeneralService.readAllInterviews(pageable);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewResponse> readOneInterview(@PathVariable Long interviewId) {
//       assume user id is 1
        InterviewResponse body = interviewGeneralService.readOneInterview(interviewId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/interviews/draft")
    public ResponseEntity<InterviewDraftResponse> createInterviewDraft() {
//       assume user id is 1
        Interview interview = interviewUploadService.createInterviewDraft(1L);

        String videoUrl = interviewUploadService.generatePresignedPost(interview.getVideoKey());
        String thumbnailUrl = interviewUploadService.generatePresignedPost(interview.getThumbnailKey());

        InterviewDraftResponse body = new InterviewDraftResponse(new InterviewDraftResponse.InterviewDraftBody(interview.getId()),
                new InterviewDraftResponse.UrlBody(videoUrl, thumbnailUrl));

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/api/interviews/{interviewId}")
    public InterviewResponse completeInterview(@PathVariable Long interviewId, @RequestBody InterviewUploadRequestDto requestDto) {

//       assume user id is 1, nickname is TestNickname
        return interviewUploadService.completeInterview(1L, interviewId, requestDto);
    }

    @PutMapping("/api/interviews/{interviewId}")
    public InterviewResponse update(@PathVariable Long interviewId, @RequestBody InterviewUpdateRequestDto requestDto) {
//       assume user id is 1, nickname is TestNickname
        return interviewGeneralService.updateInterview(interviewId, requestDto);
    }

    @DeleteMapping("/api/interviews/{interviewId}")
    public InterviewResponse deleteInterview(@PathVariable Long interviewId) {
//       assume user id is 1, nickname is TestNickname
        return interviewGeneralService.deleteInterview(interviewId, 1L);
    }





}
