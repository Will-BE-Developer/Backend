package com.sparta.willbe.user.controller;

import com.sparta.willbe._global.pagination.exception.PaginationPerInvalidException;
import com.sparta.willbe.interview.dto.InterviewListResponseDto;
import com.sparta.willbe.user.exception.UserUnauthorizedException;
import com.sparta.willbe.user.service.mypageService.UserMypageService;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.advice.RestException;
import com.sparta.willbe.interview.service.InterviewMyPageService;
import com.sparta.willbe.user.dto.UserInfoResponseDto;
import com.sparta.willbe.user.dto.UserRequestDto;
import com.sparta.willbe.user.model.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserMyPageController {
    private final UserMypageService userMypageService;
    private final InterviewMyPageService interviewMyPageService;

    @GetMapping("/api/users/me/interviews")
    @ApiOperation(value = "내 인터뷰 목록 불러오기")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity<InterviewListResponseDto> readMyInterviews(@RequestParam(value = "per", defaultValue = "6") int per,
                                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                                     @RequestParam(value = "sort", defaultValue = "new") String sort,
                                                                     @AuthenticationPrincipal User user) {
        if (per < 1) {
            throw new PaginationPerInvalidException();
        }

        if (user == null) {
            throw new UserUnauthorizedException();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " READ ALL MY INTERVIEWS");

        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());

        InterviewListResponseDto body = interviewMyPageService.readAllMyInterviews(pageable, loginUserId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/api/users/me/scraps")
    @ApiOperation(value = "내 스크랩 목록 불러오기")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity<InterviewListResponseDto> readScrapInterviews(@RequestParam(value = "per", defaultValue = "6") int per,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "sort", defaultValue = "new") String sort,
                                                                   @AuthenticationPrincipal User user) {
        if (per < 1) {
            throw new PaginationPerInvalidException();
        }

        if (user == null) {
            throw ErrorMessage.UNAUTHORIZED_USER.throwError();
        }
        Long loginUserId = user.getId();

        log.info("UID " + loginUserId + " READ ALL MY INTERVIEWS");

        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());

        InterviewListResponseDto body = interviewMyPageService.readAllMyScraps(pageable, loginUserId);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //마이페이지 - 사용자 프로필 정보 수정
    @ResponseBody
    @PutMapping(value = "/api/users/me", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "내 정보 수정")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity editUserInfo (@RequestPart(value="nickname", required = false) String nickname,
                                        @RequestPart(value="githubLink", required = false) String githubLink,
                                        @RequestPart(value="introduce", required = false) String introduce,
                                        @RequestPart(value="profileImage", required = false) MultipartFile profileImage,
                                        @RequestPart(value="profileImage", required = false) String profileImageString,
                                        @AuthenticationPrincipal User user) throws IOException {

        log.info("profileImageString: {}", profileImageString);

        if(nickname != null){
            nickname = nickname.replaceAll("^\"|\"$", "");
        }
        if(githubLink != null){
            githubLink = githubLink.replaceAll("^\"|\"$", "");
        }
        if(introduce != null){
            introduce = introduce.replaceAll("^\"|\"$", "");
        }

        log.info("UID "+user.getId()+" CHANGE PROFILE");

        UserRequestDto requestDto = new UserRequestDto(nickname, githubLink, profileImage, introduce);
        requestDto.setProfileImage(profileImage);

        UserInfoResponseDto userInfoResponseDto = userMypageService.save(requestDto, user, profileImageString);

        return new ResponseEntity(userInfoResponseDto, HttpStatus.OK);
    }



}
