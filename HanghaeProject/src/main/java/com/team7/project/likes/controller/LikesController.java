package com.team7.project.likes.controller;

import com.team7.project.likes.dto.LikeRequestDto;
import com.team7.project.likes.dto.LikesResponseDto;
import com.team7.project.likes.service.LikesService;
import com.team7.project.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.team7.project.advice.ErrorMessage.UNAUTHORIZED_USER;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/api/likes")
    public ResponseEntity<LikesResponseDto> AddLikes(@AuthenticationPrincipal User user,
                                                     @RequestBody LikeRequestDto likeRequestDto){

        if(user ==null){
            throw UNAUTHORIZED_USER.throwError();
        }
        log.info("Current user is {}" ,user.getNickname());

         LikesResponseDto likesResponseDto =  likesService.addLike(
                 likeRequestDto.getInterviewId(),
                 user,
                 likeRequestDto.getTime(),
                 likeRequestDto.getCount());
         return new ResponseEntity<LikesResponseDto>(likesResponseDto,HttpStatus.OK);
    }
    @GetMapping("/api/likes/{interviewId}")
    public ResponseEntity<LikesResponseDto> AddLikes(@AuthenticationPrincipal User user, @PathVariable Long interviewId){
        LikesResponseDto likesResponseDto =  likesService.getLike(interviewId,user);
        return new ResponseEntity<LikesResponseDto>(likesResponseDto,HttpStatus.OK);
    }
}
