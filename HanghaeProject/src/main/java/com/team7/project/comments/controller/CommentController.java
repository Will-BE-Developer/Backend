package com.team7.project.comments.controller;

import com.team7.project.comments.dto.CommentListDto;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.service.CommentService;
import com.team7.project.user.model.User;
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
    public ResponseEntity makeCommentList(@PathVariable Long interviewId,
                                          @AuthenticationPrincipal User user,
                                          @RequestParam(value = "per", defaultValue = "10") int per,
                                          @RequestParam(value = "page", defaultValue = "1") int page){

        CommentListDto commentListDto = commentService.makeCommentList(interviewId, user, per, page);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }

    @PostMapping("/api/comments")
    public ResponseEntity saveComment(@RequestBody CommentRequestDto requestDto,
                                      @AuthenticationPrincipal User user) {

        Comment comment = commentService.saveComment(requestDto, user);
        // 기존 response
        //CommentResponseDto responseDto = new CommentResponseDto(comment, true);

        // 댓글 리스트 response
        int page = commentService.getCurrentCommentPage(comment, "save");
        Long interviewId = comment.getInterview().getId();
        int per = 10;
        CommentListDto commentListDto = commentService.makeCommentList(interviewId, user, per, page);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }

    @PutMapping("/api/comments/{commentId}")
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

