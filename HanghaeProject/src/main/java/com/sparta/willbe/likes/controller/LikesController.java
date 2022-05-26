package com.sparta.willbe.likes.controller;

import com.sparta.willbe.likes.dto.LikeRequestDto;
import com.sparta.willbe.likes.dto.LikesResponseDto;
import com.sparta.willbe.likes.service.LikesService;
import com.sparta.willbe.user.exception.UserUnauthorizedException;
import com.sparta.willbe.user.model.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.sparta.willbe.advice.ErrorMessage.UNAUTHORIZED_USER;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LikesController {

    private final LikesService likesService;

    @ApiOperation(value = "좋아요 정보 불러오기")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token")
    @GetMapping("/api/likes/{interviewId}")
    public ResponseEntity<LikesResponseDto> AddLikes(@AuthenticationPrincipal User user, @PathVariable Long interviewId){
        LikesResponseDto likesResponseDto =  likesService.getLike(interviewId,user);
        return new ResponseEntity<LikesResponseDto>(likesResponseDto,HttpStatus.OK);
    }

    @ApiOperation(value = "좋아요 추가")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    @PostMapping("/api/likes")
    public ResponseEntity<LikesResponseDto> AddLikes(@AuthenticationPrincipal User user,
                                                     @RequestBody LikeRequestDto likeRequestDto){

        if(user ==null){
            throw new UserUnauthorizedException();
        }
        log.info("Current user is {}" ,user.getNickname());

         LikesResponseDto likesResponseDto =  likesService.addLike(
                 likeRequestDto.getInterviewId(),
                 user,
                 likeRequestDto.getTime(),
                 likeRequestDto.getCount());
         return new ResponseEntity<LikesResponseDto>(likesResponseDto,HttpStatus.OK);
    }

}
