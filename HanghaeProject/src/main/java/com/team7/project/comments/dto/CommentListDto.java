package com.team7.project.comments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team7.project.comments.model.Comment;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentListDto {
    private List<ResponseComment> comments = new ArrayList<>();

    //private Page pagination;

    int totalComments;

    public void addComment(Comment comment, Boolean isMine, String profileUrl){
        this.comments.add(new ResponseComment(comment, isMine, profileUrl));

    }
    public void addNestedComment(int index, Comment comment, Boolean isMine, String profileUrl){

        this.comments.get(index).addNestedCommentWithoutNest(new NestedComment(comment, isMine, profileUrl));
    }

    private Pagination pagination;

    public void addPagination(int per, int totalCounts, int totalPages,
                              int currentPage, Integer nextPage, Boolean isLastPage) {
        this.pagination = new Pagination(per, totalCounts, totalPages,
                              currentPage, nextPage, isLastPage);
    }

    @Getter
    public class ResponseComment{
        private Long id;
        @JsonIgnore
        private User userOrigin;
        private ResponseUser user;
        private String contents;
        private Boolean isMine;
        private Long parentId;
        private Long nestedCommentsCount = 0L;
        private List<NestedComment> nestedComments = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public ResponseComment(Comment comment, Boolean isMine, String profileUrl) {
            this.id = comment.getId();
            this.userOrigin = comment.getUser();
            this.user = new ResponseUser(
                    userOrigin.getId(),
                    userOrigin.getNickname(),
                    userOrigin.getGithubLink(),
                    profileUrl,
                    userOrigin.getIntroduce());
            this.contents = comment.getContents();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            this.parentId = null;
            this.isMine = isMine;
        }

        public void addNestedCommentWithoutNest(NestedComment nestedComment){
            this.nestedComments.add(nestedComment);
            this.nestedCommentsCount += 1;
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
    @Getter
    public class NestedComment{
        private Long id;
        @JsonIgnore
        private User userOrigin;
        private ResponseUser user;
        private String contents;
        private Boolean isMine;
        private Long parentId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public NestedComment(Comment comment, Boolean isMine, String profileUrl){
            this.id = comment.getId();
            this.userOrigin = comment.getUser();
            this.user = new ResponseUser(
                    userOrigin.getId(),
                    userOrigin.getNickname(),
                    userOrigin.getGithubLink(),
                    profileUrl,
                    userOrigin.getIntroduce());
            this.contents = comment.getContents();
            this.createdAt = comment.getCreatedAt();
            this.modifiedAt = comment.getModifiedAt();
            this.parentId = comment.getRootId();
            this.isMine = isMine;
        }
    }

    @Getter
    @AllArgsConstructor
    public class Pagination{
        private int per;
        private int totalCounts;
        private int totalPages;
        private int currentPage;
        private Integer nextPage;
        private Boolean isLastPage;
    }

}
