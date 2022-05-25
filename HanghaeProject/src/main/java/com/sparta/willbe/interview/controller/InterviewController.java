package com.sparta.willbe.interview.controller;

import com.sparta.willbe._global.pagination.exception.PaginationCategoryInvalidException;
import com.sparta.willbe._global.pagination.exception.PaginationPerInvalidException;
import com.sparta.willbe.interview.dto.*;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.advice.RestException;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.service.InterviewService;
import com.sparta.willbe.interview.service.InterviewUploadService;
import com.sparta.willbe.user.exception.UserUnauthorizedException;
import com.sparta.willbe.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class InterviewController {
    private final InterviewService interviewService;
    private final InterviewUploadService interviewUploadService;

    @GetMapping("/api/interviews")
    public ResponseEntity<InterviewListResponseDto> readInterviews(@RequestParam(value = "per", defaultValue = "6") int per,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "sort", defaultValue = "최신순") String sort,
                                                                   @RequestParam(value = "filter", defaultValue = "전체보기") String filter,
                                                                   @AuthenticationPrincipal User user) {

        Long loginUserId = user == null ? null : user.getId();
        log.info("UID " + loginUserId + " READ ALL INTERVIEWS WITH PER " + per + " PAGE " + page + " SORT " + sort + " FILTER " + filter);

        List<CategoryEnum> categoryEnums = Arrays.asList(CategoryEnum.values());

        if (per < 1) {
            log.error("{}(per)는 0보다 커야 합니다.",per);
            throw new PaginationPerInvalidException();
        }

        if(filter.equals("전체보기") == false){
            boolean isFilterValid = EnumUtils.isValidEnum(CategoryEnum.class, filter);
            if(isFilterValid == false){
                log.error("{} 라는 잘못된 카테고리를 입력했습니다.",filter);
                throw new PaginationCategoryInvalidException();
            }
        }

        // note that pageable start with 0
        Pageable pageable = sort.equals("오래된순") ?
                PageRequest.of(page - 1, per, Sort.by("createdAt").ascending()) :
                PageRequest.of(page - 1, per, Sort.by("createdAt").descending());


        InterviewListResponseDto body = interviewService.readAllInterviews(loginUserId, sort, filter, pageable);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> readOneInterview(@PathVariable Long interviewId,
                                                                     @AuthenticationPrincipal User user) {

        Long loginUserId = user == null ? null : user.getId();

        log.info("UID " + loginUserId + " READ INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewService.readOneInterview(interviewId, loginUserId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/api/interviews/draft")
    public ResponseEntity<InterviewDraftResponseDto> createInterviewDraft(@AuthenticationPrincipal User user) {

        if (user == null) {
            throw new UserUnauthorizedException();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " INIT POST INTERVIEW");

        Interview interview = interviewUploadService.createInterviewDraft(loginUserId);

        String videoUrl = interviewUploadService.getPresignedPost(interview.getVideoKey());
        String thumbnailUrl = interviewUploadService.getPresignedPost(interview.getThumbnailKey());

        InterviewDraftResponseDto body = new InterviewDraftResponseDto(new InterviewDraftResponseDto.InterviewDraftBody(interview.getId()),
                new InterviewDraftResponseDto.UrlBody(videoUrl, thumbnailUrl));

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> completeInterview(@PathVariable Long interviewId,
                                                                      @RequestBody InterviewPostRequestDto requestDto,
                                                                      @AuthenticationPrincipal User user) throws IOException {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " COMPLETE POST INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewUploadService.completeInterview(loginUserId, interviewId, requestDto);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> updateInterview(@PathVariable Long interviewId,
                                                                    @RequestBody InterviewUpdateRequestDto requestDto,
                                                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " UPDATE INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewService.updateInterview(loginUserId, interviewId, requestDto);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/api/interviews/{interviewId}")
    public ResponseEntity<InterviewInfoResponseDto> deleteInterview(@PathVariable Long interviewId,
                                                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " DELETE INTERVIEW " + interviewId);

        InterviewInfoResponseDto body = interviewService.deleteInterview(loginUserId, interviewId);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
