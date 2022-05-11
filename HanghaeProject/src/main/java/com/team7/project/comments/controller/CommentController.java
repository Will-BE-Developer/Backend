package com.team7.project.comments.controller;

import com.team7.project.advice.RestException;
import com.team7.project.comments.dto.CommentListDto;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.dto.CommentResponseDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.service.CommentService;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ResponseEntity saveComment(@RequestBody CommentRequestDto requestDto,
                                      @AuthenticationPrincipal User user) {

        Comment comment = commentService.saveComment(requestDto, user);
        CommentResponseDto responseDto = new CommentResponseDto(comment, true);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity editComment(@PathVariable Long commentId,
                                      @RequestBody CommentRequestDto requestDto,
                                      @AuthenticationPrincipal User user) {

        Comment editedComment = commentService.editComment(commentId, requestDto, user);
        CommentResponseDto responseDto = new CommentResponseDto(editedComment, true);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId,
                                        @AuthenticationPrincipal User user) {

        Comment comment = commentService.deleteComment(commentId, user);
        CommentResponseDto responseDto = new CommentResponseDto(comment, true);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @GetMapping("/api/comments/{interviewId}")
    public ResponseEntity makeCommentList(@PathVariable Long interviewId,
                                          @AuthenticationPrincipal User user,
                                          @RequestParam(value = "per", defaultValue = "5") int per,
                                          @RequestParam(value = "page", defaultValue = "1") int page){
        if (per < 1) {
            throw new RestException(HttpStatus.BAD_REQUEST, "한 페이지 단위(per)는 0보다 커야 합니다.");
        }
        // note that pageable start with 0
        Pageable pageable = PageRequest.of(page - 1, per, Sort.by("createdAt").descending());

        //로그인 안했으면 isMine null
        Boolean isMine = null;

        CommentListDto commentListDto = new CommentListDto();
        
        //피드백 조회
        //List<Comment> commentList = commentService.getListOfCommentOfInterview(interviewId);
        //List<Comment> commentList = commentService.getListOfCommentOfInterview(interviewId, pageable);
        List<Comment> commentList = commentService.getListOfCommentOfInterview(interviewId, per, page);

        System.out.println(commentList);
        for( Comment eachComment : commentList){
            System.out.println(eachComment.toString());
            if (user != null){
                isMine = user.getId().equals(eachComment.getUser().getId());
            }
            commentListDto.addComment(eachComment, isMine);
        }

        //피드백의 댓글 조회 + 대댓글 수
        List<Comment> nestedCommentList = commentService.getListOfCommentOfComment(interviewId);

        for( Comment eachComment : nestedCommentList){
            System.out.println("대댓글: " + eachComment.toString());

            Long RootId = eachComment.getRootId();

            List<Comment> result = commentList.stream()
                    .filter(a -> Objects.equals(a.getId(), RootId))
                    .collect(Collectors.toList());
            System.out.println("부모 댓글 조회: " + result);

            if (user != null) {
                isMine = user.getId().equals(eachComment.getUser().getId());
            }
            List<CommentListDto.ResponseComment> commentListInDto = commentListDto.getComments();
            for (CommentListDto.ResponseComment eachCommentDto: commentListInDto){
                if (eachCommentDto.getId().equals(RootId)){
                    int index = commentListDto.getComments().indexOf(eachCommentDto);
                    System.out.println("nested 넣을 댓글 목록의 index: " + index);
                    commentListDto.addNestedComment(index, eachComment, isMine);
                }
            }
        }
        int totalCounts = commentList.size();
        int totalPages = (int) Math.ceil(totalCounts/per + 1);
        int currentPage = page;
        Boolean isLastPage = false;
        int nextPage = 0;

        if (page == totalPages){
            isLastPage = true;
        }else{
            isLastPage = false;
        }

        if (isLastPage == true){
            nextPage = page;
        }else{
            nextPage = page + 1;
        }

        commentListDto.addPagination(per, totalCounts, totalPages, currentPage, nextPage, isLastPage);

        return new ResponseEntity(commentListDto, HttpStatus.OK);
    }
}
