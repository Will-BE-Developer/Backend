package com.sparta.willbe.comments.controller;

import com.sparta.willbe.comments.service.CommentService;
import com.sparta.willbe.comments.dto.CommentListDto;
import com.sparta.willbe.comments.dto.CommentRequestDto;
import com.sparta.willbe.comments.model.Comment;
import com.sparta.willbe.user.model.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @GetMapping("/api/comments/{interviewId}")
    @ApiOperation(value = "댓글 목록 불러오기")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token")
    public ResponseEntity makeCommentList(@PathVariable Long interviewId,
                                          @AuthenticationPrincipal User user,
                                          @RequestParam(value = "per", defaultValue = "10") int per,
                                          @RequestParam(value = "page", defaultValue = "1") int page){

        CommentListDto commentListDto = commentService.makeCommentList(interviewId, user, per, page);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }

    @PostMapping("/api/comments")
    @ApiOperation(value = "댓글 쓰기")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity saveComment(@RequestBody CommentRequestDto requestDto,
                                      @AuthenticationPrincipal User user) {

        Comment comment = commentService.saveComment(requestDto, user);

        // 댓글 리스트 response
        int page = commentService.getCurrentCommentPage(comment, "save");
        Long interviewId = comment.getInterview().getId();
        int per = 10;
        CommentListDto commentListDto = commentService.makeCommentList(interviewId, user, per, page);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }

    @PutMapping("/api/comments/{commentId}")
    @ApiOperation(value = "댓글 수정")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity editComment(@PathVariable Long commentId,
                                      @RequestBody CommentRequestDto requestDto,
                                      @AuthenticationPrincipal User user) {

        Comment editedComment = commentService.editComment(commentId, requestDto, user);

        // 댓글 리스트 response
        int page = commentService.getCurrentCommentPage(editedComment, "edit");
        Long interviewId = editedComment.getInterview().getId();
        int per = 10;
        CommentListDto commentListDto = commentService.makeCommentList(interviewId, user, per, page);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }

    @DeleteMapping("/api/comments/{commentId}")
    @ApiOperation(value = "댓글 삭제")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token", required = true)
    public ResponseEntity deleteComment(@PathVariable Long commentId,
                                        @AuthenticationPrincipal User user) {

        Comment comment = commentService.deleteComment(commentId, user);

        // 댓글 리스트 response
        int page = commentService.getCurrentCommentPage(comment, "delete");
        Long interviewId = comment.getInterview().getId();
        int per = 10;
        CommentListDto commentListDto = commentService.makeCommentList(interviewId, user, per, page);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }
}

