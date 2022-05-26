package com.sparta.willbe.scrap.controller;


import com.sparta.willbe.scrap.dto.ScrapInfoResponseDto;
import com.sparta.willbe.scrap.service.ScrapService;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.user.model.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ScrapController {
    private final ScrapService scrapService;

    @PostMapping("/api/scraps/{interviewId}")
    @ApiOperation(value = "스크랩 추가")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity<ScrapInfoResponseDto> createScrap(@PathVariable Long interviewId,
                                                            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw ErrorMessage.UNAUTHORIZED_USER.throwError();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " CREATE SCRAP " + interviewId);

        ScrapInfoResponseDto body = scrapService.addScrap(user, interviewId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/api/scraps/{interviewId}")
    @ApiOperation(value = "스크랩 삭제")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity<ScrapInfoResponseDto> deleteScrap(@PathVariable Long interviewId,
                                                            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw ErrorMessage.UNAUTHORIZED_USER.throwError();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " DELETE SCRAP INTERVIEW " + interviewId);

        ScrapInfoResponseDto body = scrapService.removeScrap(user, interviewId);
        body.getScrap().setScrapsCount(scrapService.getScrapCount(interviewId));
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
