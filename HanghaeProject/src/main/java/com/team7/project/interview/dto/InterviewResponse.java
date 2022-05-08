package com.team7.project.interview.dto;

import com.team7.project.interview.model.Interview;
import com.team7.project.question.dto.QuestionResponseDto;
import com.team7.project.question.model.Question;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@Builder
public class InterviewResponse {
    private InterviewResponse.Data interview;


//    need refactoring
    @Getter
    @AllArgsConstructor
    @Builder
    public static class UesrBody{
        private Long id;
        private String nickName;
        private String githubLink;
        private String profileImageUrl;
        private String introduce;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Data{
        private Long id;

        private String video;
        private String thumbnail;

        private QuestionResponseDto.data question;
        private UesrBody user;

        private String badge;
        private String note;
        private Boolean scrapsMe;
        private Long scrapsCount;
        private Long likesCount;
        private Boolean isPublic;

        private Boolean isMine;

        private String createdAt;
        private String updatedAt;
    }

    public InterviewResponse(Interview interview, String videoUrl, String imageUrl){
        Question question = interview.getQuestion();
        Long questionId = question.getId();
        String questionCategory = question.getCategory().name();
        String questionContents = question.getContents();
        String questionReference = question.getReference();

//      must be refactored
//        User user = interview.getUser();
        Long userId = 1L;
        String userNickname = "TestNickName";
        String userGithubLink = "https://github.com/llama-ste";
        String userProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/react-deep-99.appspot.com/o/images%2F1_1650953241454?alt=media&token=7e31bc8a-352c-48bf-90e6-1fce202e8935";
        String userIntroduce = "testIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroducetestIntroduce";

        this.interview = Data.builder()
                .id(interview.getId())
                .video(videoUrl)
                .thumbnail(imageUrl)
                .question(new QuestionResponseDto.data(questionId, questionCategory, questionContents,questionReference))
                .user(new UesrBody(userId, userNickname,userGithubLink,userProfileImageUrl,userIntroduce))
                .badge(interview.getBadge())
                .note(interview.getMemo())
                .scrapsMe(false)
                .scrapsCount(0L)
                .likesCount(0L)
                .isPublic(interview.getIsPublic())
//                .isMine() must be refactored
                .isMine(true)
                .createdAt(interview.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(interview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
