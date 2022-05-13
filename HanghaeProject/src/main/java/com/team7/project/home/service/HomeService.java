package com.team7.project.home.service;

import com.team7.project.advice.RestException;
import com.team7.project.batch.BATCH_repository.BATCH_TodayQuestionRepository;
import com.team7.project.batch.BATCH_repository.BATCH_TopCategoriesRepository;
import com.team7.project.batch.BATCH_repository.BATCH_WeeklyInterviewRepository;
import com.team7.project.batch.tables.BATCH_TodayQuestion;
import com.team7.project.batch.tables.BATCH_TopCategories;
import com.team7.project.batch.tables.BATCH_WeeklyInterview;
import com.team7.project.comments.dto.CommentResponseDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.repository.CommentRepository;
import com.team7.project.interview.dto.InterviewInfoResponseDto;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.question.dto.QuestionResponseDto;
import com.team7.project.question.model.Question;
import com.team7.project.question.repostitory.QuestionRepository;
import com.team7.project.scrap.model.Scrap;
import com.team7.project.user.dto.UserInfoResponseDto;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class HomeService {
    private final BATCH_WeeklyInterviewRepository batch_weeklyInterviewRepository;
    private final QuestionRepository questionRepository;
    private final BATCH_TodayQuestionRepository batch_todayQuestionRepository;
    private final BATCH_TopCategoriesRepository batch_topCategoriesRepository;
    private final CommentRepository commentRepository;

    public List<BATCH_TopCategories> getTopCatetories() {
        List<BATCH_TopCategories> topCategories = batch_topCategoriesRepository.findAll();
        return topCategories;
    }

    public List<QuestionResponseDto> getTodayQuestion() {
        List<BATCH_TodayQuestion> todaysQuestions = batch_todayQuestionRepository.findAll();
        List<QuestionResponseDto> todaysQuestionsDto = new ArrayList<>();
        for (BATCH_TodayQuestion todayQuestion : todaysQuestions) {
            Question question = questionRepository.findById(todayQuestion.getQuestionId()).orElseThrow(
                    () -> new RestException(HttpStatus.BAD_REQUEST, "해당 질문이 존재하지 않습니다")
            );
            QuestionResponseDto n = new QuestionResponseDto(new QuestionResponseDto.data(
                    question.getId(),
                    question.getCategory().name(),
                    question.getContents(),
                    question.getReference()
            ));
            todaysQuestionsDto.add(n);
        }
        return todaysQuestionsDto;
    }

    public List<CommentResponseDto.ResponseComment> getLatestComments(User user) {
        List<Comment> comments = commentRepository.findAllByRootNameOrderByCreatedAtDesc("interview", PageRequest.of(0, 3));
        log.info("COMMENTS FOUND : {}", comments);
        List<CommentResponseDto.ResponseComment> commentResponseDtos = new ArrayList<>();
        Boolean ismine = false;
        for (Comment comment : comments) {
            if (user != null) {
                ismine = comment.getUser().getEmail() == user.getEmail();
            }
            commentResponseDtos.add(
                    new CommentResponseDto.ResponseComment(comment, ismine)
            );
        }
        return commentResponseDtos;
    }

    public List<InterviewInfoResponseDto.Data> getWeeklyInterview(User user) {

        List<BATCH_WeeklyInterview> getInterviews = batch_weeklyInterviewRepository.findAll();
        List<InterviewInfoResponseDto.Data> weeklyInterview = new ArrayList<>();
        Boolean ismine = false;
        Boolean scrapMe = false;

        for (BATCH_WeeklyInterview interview : getInterviews) {

            if (user != null) {
                ismine = interview.getUser().getEmail() == user.getEmail();
                Set<Long> userScapId = createUserScrapIds(user);
                scrapMe = userScapId.contains(interview.getId());
            }

            InterviewInfoResponseDto.Data n = InterviewInfoResponseDto.Data.builder()
                    .id(interview.getInterview().getId())
                    .video(interview.getInterview().getVideoKey())
                    .thumbnail(interview.getInterview().getThumbnailKey())
                    .question(new QuestionResponseDto.data(interview.getQuestion().getId(), interview.getQuestion().getCategory().name(), interview.getQuestion().getContents(), interview.getQuestion().getReference()))
                    .user(UserInfoResponseDto.UserBody.builder()
                            .githubLink(interview.getInterview().getUser().getGithubLink())
                            .id(interview.getInterview().getUser().getId())
                            .nickname(interview.getInterview().getUser().getNickname())
                            .profileImageUrl(interview.getInterview().getUser().getProfileImageUrl())
                            .introduce(interview.getInterview().getUser().getIntroduce())
                            .build())
                    .badge(interview.getBadge())
                    .note(interview.getInterview().getMemo())
                    .scrapsMe(scrapMe)
                    .scrapsCount(interview.getScrapCount())
                    .likesCount(0L)
                    .isPublic(interview.getInterview().getIsPublic())
                    .isMine(ismine)
                    .createdAt(interview.getInterview().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .updatedAt(interview.getInterview().getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            weeklyInterview.add(n);
        }
        return weeklyInterview;
    }

    public Set<Long> createUserScrapIds(User user) {
        Set<Long> userScrapsId = new HashSet<>();
        if (user != null) {
            for (Scrap scrap : user.getScraps()) {
                userScrapsId.add(scrap.getInterview().getId());
            }
        }
        return userScrapsId;


    }
}
