package com.sparta.willbe.home.service;

import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.batch.repository.TodayQuestionRepository;
import com.sparta.willbe.batch.repository.TopCategoriesRepository;
import com.sparta.willbe.batch.repository.WeeklyInterviewRepository;
import com.sparta.willbe.batch.tables.TopCategories;
import com.sparta.willbe.batch.tables.WeeklyInterview;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.interview.service.InterviewService;
import com.sparta.willbe.question.exception.QuestionNotFoundException;
import com.sparta.willbe.question.repostitory.QuestionRepository;
import com.sparta.willbe.scrap.model.Scrap;
import com.sparta.willbe.user.dto.UserInfoResponseDto;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import com.sparta.willbe.batch.tables.TodayQuestion;
import com.sparta.willbe.comments.dto.CommentResponseDto;
import com.sparta.willbe.comments.model.Comment;
import com.sparta.willbe.comments.repository.CommentRepository;
import com.sparta.willbe.question.dto.QuestionResponseDto;
import com.sparta.willbe.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private final InterviewService interviewService;
    private final WeeklyInterviewRepository weeklyInterviewRepository;
    private final QuestionRepository questionRepository;
    private final TodayQuestionRepository todayQuestionRepository;
    private final TopCategoriesRepository topCategoriesRepository;
    private final CommentRepository commentRepository;
    private final InterviewRepository interviewRepository;

    public List<TopCategories> getTopCatetories() {
        List<TopCategories> topCategories = topCategoriesRepository.findTop6ByOrderByCreatedAtDesc();
        return topCategories;
    }

    public List<QuestionResponseDto> getTodayQuestion() {
        List<TodayQuestion> todaysQuestions = todayQuestionRepository.findTop3ByOrderByCreatedAtDesc();
        List<QuestionResponseDto> todaysQuestionsDto = new ArrayList<>();
        for (TodayQuestion todayQuestion : todaysQuestions) {
            Question question = questionRepository.findById(todayQuestion.getQuestion().getId()).orElseThrow(
                    QuestionNotFoundException::new
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
        List<Comment> comments = commentRepository.findTop4ByRootNameOrderByCreatedAtDesc("interview");

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
        List<Interview> latstInterview = interviewRepository.findTop4ByIsDoneTrueAndIsPublicTrueOrderByCreatedAtDesc();
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
                    .video(interviewService.getProfileImageUrl(interview.getVideoKey()))
                    .thumbnail(interviewService.getProfileImageUrl(interview.getThumbnailKey()))
                    .question(new QuestionResponseDto.data(interview.getQuestion().getId(),
                            interview.getQuestion().getCategory().name(),
                            interview.getQuestion().getContents(),
                            interview.getQuestion().getReference()))
                    .user(UserInfoResponseDto.UserBody.builder()
                            .githubLink(interview.getUser().getGithubLink())
                            .id(interview.getUser().getId())
                            .nickname(interview.getUser().getNickname())
                            .profileImageUrl(interviewService
                                    .getProfileImageUrl(interview.getUser().getProfileImageUrl()))
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

        final long WEEK = 1000L* 60 * 60* 24*7;
        List<WeeklyInterview> getInterviews = weeklyInterviewRepository.findByCreatedAtBetween(LocalDateTime.now().minus(1, ChronoUnit.WEEKS),LocalDateTime.now());
        List<InterviewInfoResponseDto.Data> weeklyInterview = new ArrayList<>();
        Boolean ismine = false;
        Boolean scrapMe = false;
        log.debug("WeeklyInterview HOMESERVICE >> get {} INTERVIEWS ",getInterviews.size());
        for (WeeklyInterview interview : getInterviews) {

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
                    .video(interviewService.getProfileImageUrl(interview.getInterview().getVideoKey()))
                    .thumbnail(interviewService.getProfileImageUrl(interview.getInterview().getThumbnailKey()))
                    .question(new QuestionResponseDto.data(interview.getInterview().getQuestion().getId(),
                            interview.getInterview().getQuestion().getCategory().name(),
                            interview.getInterview().getQuestion().getContents(),
                            interview.getInterview().getQuestion().getReference()))
                    .user(UserInfoResponseDto.UserBody.builder()
                            .githubLink(interview.getInterview().getUser().getGithubLink())
                            .id(interview.getInterview().getUser().getId())
                            .nickname(interview.getInterview().getUser().getNickname())
                            .profileImageUrl(interviewService.getProfileImageUrl(interview.getInterview().getUser().getProfileImageUrl()))
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
