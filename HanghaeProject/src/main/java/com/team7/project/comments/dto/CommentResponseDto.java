package com.team7.project.comments.dto;

import com.team7.project.comments.model.Comment;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
//@AllArgsConstructor
public class CommentResponseDto {
    //private Long id;
    //private User user;
    //private String contents;
    private Comment comment;
    private Boolean isMine;
    //private LocalDateTime createdAt;
    //private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment, Boolean isMine) {
        this.comment = comment;
        this.isMine = isMine;
    }
}
