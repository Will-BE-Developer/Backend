package com.team7.project.comments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team7.project.comments.model.Comment;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentListDto {
    private List<ResponseComment> comment = new ArrayList<>();
    //private Page pagination;

//    public CommentListDto(Comment comment, Boolean isMine){
//        this.comment.add(new ResponseComment(comment, isMine));
//    }
    public void addComment(Comment comment, Boolean isMine){
        this.comment.add(new ResponseComment(comment, isMine));
    }
    public void addNestedComment(int index, Comment comment, Boolean isMine){

        //this.comment.get(index).addNestedComment(comment, isMine);
        this.comment.get(index).addNestedComment(new ResponseComment(comment, isMine));
    }
    @Getter
    public class ResponseComment{
        private Long id;
        @JsonIgnore
        private User userOrigin;
        private ResponseUser user;
        private String contents;
        private Boolean isMine;
        private Long nestedCommentsCount = 0L;
        //private List<ResponseComment> nestedComments;
        private List<ResponseComment> nestedComments = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public ResponseComment(Comment comment, Boolean isMine) {
            this.id = comment.getId();
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

//        public void addNestedComment(ResponseComment comment, boolean isMine){
//            this.nestedComments.add(comment);
//            if (nestedComments != null && !nestedComments.isEmpty()){
//                int lastIndex = nestedComments.size() - 1;
//                this.nestedComments.get(lastIndex).isMine = isMine;
//            }
//        }

        public void addNestedComment(ResponseComment responseComment) {
            this.nestedComments.add(responseComment);
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


}
