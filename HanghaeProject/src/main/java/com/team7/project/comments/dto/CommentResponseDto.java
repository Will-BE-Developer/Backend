package com.team7.project.comments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team7.project.comments.model.Comment;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;



@Getter
@NoArgsConstructor
public class CommentResponseDto {
    //private Comment comment;
    private Long id;
    @JsonIgnore
    private User userOrigin;
    private ResponseUser user;
    private String contents;
    private Boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment, Boolean isMine) {
        //this.comment = comment;
        this.id = comment.getId();
        //this.user = comment.getUser();
        this.userOrigin = comment.getUser();
        this.user = new ResponseUser(
                userOrigin.getId(),
                userOrigin.getNickname(),
                userOrigin.getGithubLink(),
                userOrigin.getProfileImageUrl(),
                userOrigin.getIntroduce());
        this.contents = comment.getContents();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.isMine = isMine;
    }

//    public void setComment(Comment comment) {
//        this.comment = comment;
//    }
//    public void setMine(Boolean mine) {
//        isMine = mine;
//    }
    @Getter
    @AllArgsConstructor
    public class ResponseUser{
        private Long id;
        private String nickname;
        private String githubLink;
        private String profileImageUrl;
        private String introduce;
    }
}

