package com.team7.project.comments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team7.project.comments.model.Comment;
import com.team7.project.user.dto.UserInfoResponseDto;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private ResponseComment comment;

    public CommentResponseDto(Comment comment, Boolean isMine){
        this.comment = new ResponseComment(comment, isMine);
    }

    @Getter
    @Setter
    public static class ResponseComment{
        private Long id;
        @JsonIgnore
        private User userOrigin;
        private UserInfoResponseDto.UserBody user;
        private String contents;
        private Boolean isMine;
        private Long parentId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public ResponseComment(Comment comment, Boolean isMine) {
            this.id = comment.getId();
            this.userOrigin = comment.getUser();
            this.user = UserInfoResponseDto.UserBody.builder()
                    .introduce(userOrigin.getIntroduce())
                    .profileImageUrl(userOrigin.getProfileImageUrl())
                    .nickname(userOrigin.getNickname())
                    .githubLink(userOrigin.getGithubLink())
                    .id(userOrigin.getId())
                    .build();
            this.contents = comment.getContents();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            this.parentId = comment.getRootId();
            this.isMine = isMine;
        }
    }
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

