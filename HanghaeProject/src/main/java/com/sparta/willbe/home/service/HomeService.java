package com.sparta.willbe.home.service;

import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.batch.BATCH_repository.BATCH_TodayQuestionRepository;
import com.sparta.willbe.batch.BATCH_repository.BATCH_TopCategoriesRepository;
import com.sparta.willbe.batch.BATCH_repository.BATCH_WeeklyInterviewRepository;
import com.sparta.willbe.batch.tables.BATCH_TopCategories;
import com.sparta.willbe.batch.tables.BATCH_WeeklyInterview;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.interview.service.InterviewGeneralService;
import com.sparta.willbe.question.repostitory.QuestionRepository;
import com.sparta.willbe.scrap.model.Scrap;
import com.sparta.willbe.user.dto.UserInfoResponseDto;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import com.sparta.willbe.batch.tables.BATCH_TodayQuestion;
import com.sparta.willbe.comments.dto.CommentResponseDto;
import com.sparta.willbe.comments.model.Comment;
import com.sparta.willbe.comments.repository.CommentRepository;
import com.sparta.willbe.question.dto.QuestionResponseDto;
import com.sparta.willbe.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    private final UserRepository userRepository;
    private final InterviewGeneralService interviewGeneralService;
    private final BATCH_WeeklyInterviewRepository batch_weeklyInterviewRepository;
    private final QuestionRepository questionRepository;
    private final BATCH_TodayQuestionRepository batch_todayQuestionRepository;
    private final BATCH_TopCategoriesRepository batch_topCategoriesRepository;
    private final CommentRepository commentRepository;
    private final InterviewRepository interviewRepository;

    public List<BATCH_TopCategories> getTopCatetories() {
        List<BATCH_TopCategories> topCategories = batch_topCategoriesRepository.findAll();
        return topCategories;
    }

    public List<QuestionResponseDto> getTodayQuestion() {
        List<BATCH_TodayQuestion> todaysQuestions = batch_todayQuestionRepository.findAll();
        List<QuestionResponseDto> todaysQuestionsDto = new ArrayList<>();
        for (BATCH_TodayQuestion todayQuestion : todaysQuestions) {
            Question question = questionRepository.findById(todayQuestion.getQuestionId()).orElseThrow(
                    () -> ErrorMessage.NOT_FOUND_QUESTION.throwError()
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
        List<Comment> comments = commentRepository.findAllByRootNameOrderByCreatedAtDesc("interview", PageRequest.of(0, 4));

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

    public List<InterviewInfoResponseDto.Data> getLatestInterview(User user) {
        List<Interview> latstInterview = interviewRepository.findAllByIsDoneAndIsPublicOrderByCreatedAtDesc(true, true, PageRequest.of(0, 4));
        List<InterviewInfoResponseDto.Data> latestInterviewDto = new ArrayList<>();
        Boolean ismine = false;
        Boolean scrapMe = false;

        for (Interview interview : latstInterview) {

            if (user != null) {
                User loginUser = userRepository.getById(user.getId());
                ismine = interview.getUser().getEmail() == loginUser.getEmail();
                Set<Long> userScapId = createUserScrapIds(loginUser);
                scrapMe = userScapId.contains(interview.getId());
            }

            int commentsCount = commentRepository.countByInterview_Id(interview.getId());

            InterviewInfoResponseDto.Data n = InterviewInfoResponseDto.Data.builder()
                    .id(interview.getId())
                    .video(interviewGeneralService.generateProfileImageUrl(interview.getVideoKey()))
                    .thumbnail(interviewGeneralService.generateProfileImageUrl(interview.getThumbnailKey()))
                    .question(new QuestionResponseDto.data(interview.getQuestion().getId(), interview.getQuestion().getCategory().name(), interview.getQuestion().getContents(), interview.getQuestion().getReference()))
                    .user(UserInfoResponseDto.UserBody.builder()
                            .githubLink(interview.getUser().getGithubLink())
                            .id(interview.getUser().getId())
                            .nickname(interview.getUser().getNickname())
                            .profileImageUrl(interviewGeneralService.generateProfileImageUrl(interview.getUser().getProfileImageUrl()))
                            .introduce(interview.getUser().getIntroduce())
                            .build())
                    .badge(interview.getBadge())
                    .note(interview.getMemo())
                    .scrapsMe(scrapMe)
                    .scrapsCount((long) (interview.getScraps().size()))
                    .commentsCount((long) commentsCount)
                    .likesCount(0L)
                    .isPublic(interview.getIsPublic())
                    .isMine(ismine)
                    .createdAt(interview.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .updatedAt(interview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            latestInterviewDto.add(n);
        }
        return latestInterviewDto;
    }

    public List<InterviewInfoResponseDto.Data> getWeeklyInterview(User user) {

        List<BATCH_WeeklyInterview> getInterviews = batch_weeklyInterviewRepository.findAll();
        List<InterviewInfoResponseDto.Data> weeklyInterview = new ArrayList<>();
        Boolean ismine = false;
        Boolean scrapMe = false;

        for (BATCH_WeeklyInterview interview : getInterviews) {

            if (user != null) {
                User loginUser = userRepository.getById(user.getId());
                ismine = interview.getInterview().getUser().getEmail() == loginUser.getEmail();
                Set<Long> userScapId = createUserScrapIds(loginUser);
                scrapMe = userScapId.contains(interview.getId());
            }

            int commentsCount = commentRepository.countByInterview_Id(interview.getInterview().getId());

            String weeklyBadge = interview.getWeeklyBadge();
            String[] weekKorean = {"첫째주","둘째주","셋째주","넷째주","다섯째주"};
            int week = Integer.parseInt(weeklyBadge.substring(3,4)) -1;
            String weeklyInterviewKing = weeklyBadge.substring(0,3) + weekKorean[week] + " 면접왕" + weeklyBadge.substring(6,9);

            InterviewInfoResponseDto.Data n = InterviewInfoResponseDto.Data.builder()
                    .id(interview.getInterview().getId())
                    .video(interviewGeneralService.generateProfileImageUrl(interview.getInterview().getVideoKey()))
                    .thumbnail(interviewGeneralService.generateProfileImageUrl(interview.getInterview().getThumbnailKey()))
                    .question(new QuestionResponseDto.data(interview.getInterview().getQuestion().getId(),
                            interview.getInterview().getQuestion().getCategory().name(),
                            interview.getInterview().getQuestion().getContents(),
                            interview.getInterview().getQuestion().getReference()))
                    .user(UserInfoResponseDto.UserBody.builder()
                            .githubLink(interview.getInterview().getUser().getGithubLink())
                            .id(interview.getInterview().getUser().getId())
                            .nickname(interview.getInterview().getUser().getNickname())
                            .profileImageUrl(interviewGeneralService.generateProfileImageUrl(interview.getInterview().getUser().getProfileImageUrl()))
                            .introduce(interview.getInterview().getUser().getIntroduce())
                            .build())
                    .badge(weeklyInterviewKing)
                    .note(interview.getInterview().getMemo())
                    .scrapsMe(scrapMe)
                    .scrapsCount(interview.getScrapCount())
                    .commentsCount((long) commentsCount)
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
            User loginUser = userRepository.getById(user.getId());
            for (Scrap scrap : loginUser.getScraps()) {
                userScrapsId.add(scrap.getInterview().getId());
            }
        }
        return userScrapsId;


    }
}
