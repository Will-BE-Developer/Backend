package com.team7.project.interview.controller;

import com.team7.project.advice.RestException;
import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.dto.*;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.service.InterviewGeneralService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class InterviewController {
    private final InterviewPostService interviewPostService;
    private final InterviewGeneralService interviewGeneralService;


    @GetMapping("/api/interviews")
    public ResponseEntity<InterviewListResponseDto> readInterviews(@RequestParam(value = "per", defaultValue = "6") int per,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "sort", defaultValue = "new") String sort,
                                                                   @RequestParam(value = "filter", defaultValue = "default") String filter,
                                                                   @AuthenticationPrincipal User user) {

        Long loginUserId = user == null ? null : user.getId();
        log.info("UID " + loginUserId + " READ ALL INTERVIEWS WITH PER " + per + " PAGE " + page + " SORT " + sort + " FILTER " + filter);

        List<CategoryEnum> categoryEnums = Arrays.asList(CategoryEnum.values());

        if (per < 1) {
            log.error(per + "(per)는 0보다 커야 합니다.");
            throw new RestException(HttpStatus.BAD_REQUEST, "한 페이지 단위(per)는 0보다 커야 합니다.");
        }

//        if(filter.equals("default") != false){
//            boolean isValidFilter = categoryEnums.contains(CategoryEnum.valueOf(filter));
//            if(isValidFilter){
//                log.error(filter + "라는 잘못된 카테고리를 입력했습니다.");
//                throw new RestException(HttpStatus.NOT_FOUND, "잘못된 카테고리를 입력했습니다.");
//            }
//        }

        // note that pageable start with 0
        Pageable pageable = sort.equals("old") ?
                PageRequest.of(page - 1, per, Sort.by("createdAt").ascending()) :
                PageRequest.of(page - 1, per, Sort.by("createdAt").descending());


        InterviewListResponseDto body = interviewGeneralService.readAllInterviews(loginUserId, sort, filter, pageable);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> readOneInterview(@PathVariable Long interviewId,
                                                                     @AuthenticationPrincipal User user) {

        Long loginUserId = user == null ? null : user.getId();

        log.info("UID " + loginUserId + " READ INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewGeneralService.readOneInterview(interviewId, loginUserId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/api/interviews/draft")
    public ResponseEntity<InterviewDraftResponseDto> createInterviewDraft(@AuthenticationPrincipal User user) {

        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "로그인을 해야합니다.");
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " INIT POST INTERVIEW");

        Interview interview = interviewPostService.createInterviewDraft(loginUserId);

        String videoUrl = interviewPostService.generatePresignedPost(interview.getVideoKey());
        String thumbnailUrl = interviewPostService.generatePresignedPost(interview.getThumbnailKey());

        InterviewDraftResponseDto body = new InterviewDraftResponseDto(new InterviewDraftResponseDto.InterviewDraftBody(interview.getId()),
                new InterviewDraftResponseDto.UrlBody(videoUrl, thumbnailUrl));

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> completeInterview(@PathVariable Long interviewId,
                                                                      @RequestBody InterviewPostRequestDto requestDto,
                                                                      @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "로그인을 해야합니다.");
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " COMPLETE POST INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewPostService.completeInterview(loginUserId, interviewId, requestDto);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> updateInterview(@PathVariable Long interviewId,
                                                                    @RequestBody InterviewUpdateRequestDto requestDto,
                                                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "로그인을 해야합니다.");
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " UPDATE INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewGeneralService.updateInterview(loginUserId, interviewId, requestDto);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> deleteInterview(@PathVariable Long interviewId,
                                                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "로그인을 해야합니다.");
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " DELETE INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewGeneralService.deleteInterview(loginUserId, interviewId);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
