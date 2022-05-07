package com.team7.project.comments.controller;

import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.dto.CommentResponseDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.service.CommentService;
import com.team7.project.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/api/comments")
    public ResponseEntity registerUser(CommentRequestDto requestDto,
                                       @AuthenticationPrincipal User user) {
        // 토큰 여부 확인???
        //Long userId = user.getId();
        //commentService.saveComment(requestDto, userId);
        Comment comment = commentService.saveComment(requestDto, user);
        CommentResponseDto responseDto = new CommentResponseDto(comment, true);

        //return new ResponseEntity("{'result':'success'}", HttpStatus.OK);
        return new ResponseEntity(requestDto, HttpStatus.OK);
    }
}
