package com.team7.project.comments.controller;

//import com.fasterxml.jackson.annotation.JsonView;
//import com.monitorjbl.json.JsonView;
import com.fasterxml.jackson.annotation.JsonView;
import com.team7.project.advice.RestException;
import com.team7.project.comments.dto.CommentListDto;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.dto.CommentResponseDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.service.CommentService;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//@Controller
@RestController
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository; //temp

    @Autowired
    public CommentController(CommentService commentService, UserRepository userRepository){
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/comments")
    public ResponseEntity saveComment(@RequestBody CommentRequestDto requestDto) { //@AuthenticationPrincipal User user

        User user = userRepository.findById(1L).orElseThrow( //temp
                () -> new IllegalArgumentException("없는 사용자입니다.")
        );
        Comment comment = commentService.saveComment(requestDto, user);
        CommentResponseDto responseDto = new CommentResponseDto(comment, true);
        //CommentResponseDto responseDto = new CommentResponseDto();
        //responseDto.setComment(rootComment);
        //responseDto.setMine(true);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity editComment(@PathVariable Long commentId,
                                      @RequestBody CommentRequestDto requestDto) { //@AuthenticationPrincipal User user

        User user = userRepository.findById(1L).orElseThrow( //temp
                () -> new IllegalArgumentException("없는 사용자입니다.")
        );
        Comment editedComment = commentService.editComment(commentId, requestDto, user);
        CommentResponseDto responseDto = new CommentResponseDto(editedComment, true);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId) { //@AuthenticationPrincipal User user

        User user = userRepository.findById(1L).orElseThrow( //temp
                () -> new IllegalArgumentException("없는 사용자입니다.")
        );
        Comment comment = commentService.deleteComment(commentId);
        CommentResponseDto responseDto = new CommentResponseDto(comment, true);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @GetMapping("/api/comments/{interviewId}")
    public ResponseEntity makeCommentList(@PathVariable Long interviewId){ //@AuthenticationPrincipal User user
        User user = userRepository.findById(1L).orElseThrow( //temp
                () -> new IllegalArgumentException("없는 사용자입니다.")
        );

        CommentListDto commentListDto = new CommentListDto();
        
        //피드백 조회
        //List<Comment> commentList = commentService.getCommentList(interviewId);
        List<Comment> commentList = commentService.getListOfCommentOfInterview(interviewId);
        System.out.println(commentList);
        for( Comment eachComment : commentList){
            System.out.println(eachComment.toString());
            Boolean isMine = user.getId().equals(eachComment.getUser().getId());
            commentListDto.addComment(eachComment, isMine);
        }
        //피드백의 댓글 조회 + 대댓글 수
        List<Comment> nestedCommentList = commentService.getListOfCommentOfComment(interviewId);

        for( Comment eachComment : nestedCommentList){
            System.out.println("nestedComment: " + eachComment.toString());

            Long RootId = eachComment.getRootId();
            Comment rootComment = new Comment();
            List<Comment> result = commentList.stream()
                    .filter(a -> Objects.equals(a.getId(), RootId))
                    .collect(Collectors.toList());
            System.out.println("result" + result);

            Boolean isMine = user.getId().equals(eachComment.getUser().getId());
//            if (commentListDto.getComment().contains(result)){  //not works
//                int index = commentListDto.getComment().indexOf(result);
//                System.out.println("in contain if, index: " + index);
//                commentListDto.addNestedComment(index, eachComment, isMine);
//            }
            List<CommentListDto.ResponseComment> commentListInDto = commentListDto.getComment();
            for (CommentListDto.ResponseComment eachCommentDto: commentListInDto){
                if (eachCommentDto.getId().equals(RootId)){
                    int index = commentListDto.getComment().indexOf(eachCommentDto);
                    System.out.println("in contain if, index: " + index);
                    //commentListDto.addNestedComment(index, eachComment, isMine);
                    commentListDto.addNestedComment(index, eachComment, isMine);
                }
            }
        }

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }
}
