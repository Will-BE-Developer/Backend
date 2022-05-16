package com.team7.project.user.controller;

import com.team7.project.advice.ErrorMessage;
import com.team7.project.advice.RestException;
import com.team7.project.interview.dto.*;
import com.team7.project.interview.service.InterviewMyPageService;
import com.team7.project.interview.service.InterviewPostService;
import com.team7.project.user.dto.UserInfoResponseDto;
import com.team7.project.user.dto.UserRequestDto;
import com.team7.project.user.model.User;
import com.team7.project.user.service.mypageService.UserMypageService;
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
    private final InterviewPostService interviewPostService;
    private final InterviewMyPageService interviewMyPageService;

    @GetMapping("/api/users/me/interviews")
    public ResponseEntity<InterviewListResponseDto> readMyInterviews(@RequestParam(value = "per", defaultValue = "6") int per,
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
    public ResponseEntity<InterviewListResponseDto> readScrapInterviews(@RequestParam(value = "per", defaultValue = "6") int per,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "sort", defaultValue = "new") String sort,
                                                                   @AuthenticationPrincipal User user) {
        if (per < 1) {
            throw ErrorMessage.INVALID_PAGINATION_SIZE.throwError();
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
    public ResponseEntity editUserInfo2 (@RequestPart(value="nickname", required = false) String nickname,
                                         @RequestPart(value="githubLink", required = false) String githubLink,
                                         @RequestPart(value="introduce", required = false) String introduce,
                                         @RequestPart(value="profileImage", required = false) MultipartFile profileImage,
                                         @AuthenticationPrincipal User user) throws IOException {

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

        UserInfoResponseDto userInfoResponseDto = userMypageService.save(requestDto, user);

        return new ResponseEntity(userInfoResponseDto, HttpStatus.OK);
    }



}
