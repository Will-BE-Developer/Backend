package com.team7.project.user.controller;

import com.team7.project.advice.RestException;
import com.team7.project.interview.dto.*;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.service.InterviewGeneralService;
import com.team7.project.interview.service.InterviewMyPageService;
import com.team7.project.interview.service.InterviewPostService;
import com.team7.project.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserMyPageController {
    private final InterviewPostService interviewPostService;
    private final InterviewMyPageService interviewMyPageService;


    @GetMapping("/api/users/me/interviews")
    public ResponseEntity<InterviewListResponseDto> readMyInterviews(@RequestParam(value = "per", defaultValue = "8") int per,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "sort", defaultValue = "new") String sort,
                                                                   @AuthenticationPrincipal User user) {
        if (per < 1) {
            throw new RestException(HttpStatus.BAD_REQUEST, "한 페이지 단위(per)는 0보다 커야 합니다.");
        }

        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "로그인을 해야합니다.");
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " READ ALL MY INTERVIEWS");

        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());

        InterviewListResponseDto body = interviewMyPageService.readAllMyInterviews(pageable, loginUserId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/users/me/scraps")
    public ResponseEntity<InterviewListResponseDto> readScrapInterviews(@RequestParam(value = "per", defaultValue = "8") int per,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "sort", defaultValue = "new") String sort,
                                                                   @AuthenticationPrincipal User user) {
        if (per < 1) {
            throw new RestException(HttpStatus.BAD_REQUEST, "한 페이지 단위(per)는 0보다 커야 합니다.");
        }

        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "로그인을 해야합니다.");
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " READ ALL MY INTERVIEWS");

        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());

        InterviewListResponseDto body = interviewMyPageService.readAllMyScraps(pageable, loginUserId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


}
