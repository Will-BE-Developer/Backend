package com.sparta.willbe.home.service;

import com.sparta.willbe.batch.repository.TodayQuestionRepository;
import com.sparta.willbe.batch.repository.TopCategoriesRepository;
import com.sparta.willbe.batch.repository.WeeklyInterviewRepository;
import com.sparta.willbe.batch.tables.TopCategories;
import com.sparta.willbe.batch.tables.WeeklyInterview;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.interview.exception.InterviewNotFoundException;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class HomeService {
    private final UserRepository userRepository;
    private final InterviewService interviewService;
    private final WeeklyInterviewRepository weeklyInterviewRepository;
    private final QuestionRepository questionRepository;
    private final TodayQuestionRepository batch_todayQuestionRepository;
    private final TopCategoriesRepository batch_topCategoriesRepository;
    private final CommentRepository commentRepository;
    private final InterviewRepository interviewRepository;

    public List<TopCategories> getTopCatetories() {
        List<TopCategories> topCategories = batch_topCategoriesRepository.findTop6ByOrderByCreatedAtDesc();
        return topCategories;
    }

    public List<QuestionResponseDto> getTodayQuestion() {
        List<TodayQuestion> todaysQuestions = batch_todayQuestionRepository.findTop3ByOrderByCreatedAtDesc();
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

    public List<InterviewInfoResponseDto.Data> getLatestInterview(User user) {
        List<Interview> latstInterview = interviewRepository.findTop4ByIsDoneTrueAndIsPublicTrueAndUser_IsDeletedFalseOrderByCreatedAtDesc();
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

            int commentsCount = commentRepository.countByInterview_IdAndUser_IsDeletedFalse(interview.getId());

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

        List<WeeklyInterview> getInterviews = weeklyInterviewRepository.findByCreatedAtBetween(LocalDateTime.now().minus(1, ChronoUnit.WEEKS), LocalDateTime.now());
        List<InterviewInfoResponseDto.Data> weeklyInterview = new ArrayList<>();
        Boolean ismine = false;
        Boolean scrapMe = false;
        int ranking = 0;
        for (WeeklyInterview interview : getInterviews) {

            Interview interviewById = interviewRepository.findById(interview.getInterviewId())
                    .orElse(null);
            if (interviewById == null) {
                continue;
            }
            else if(interviewById.getUser().getIsDeleted()){
                continue;
            }
            ranking++;

            if (user != null) {
                User loginUser = userRepository.getById(user.getId());
                ismine = interviewById.getUser().getEmail() == loginUser.getEmail();
                Set<Long> userScapId = createUserScrapIds(loginUser);
                scrapMe = userScapId.contains(interview.getId());
            }

            int commentsCount = commentRepository.countByInterview_IdAndUser_IsDeletedFalse(interviewById.getId());

            String weeklyBadge = interview.getWeeklyBadge();
            String[] weekKorean = {"첫째주", "둘째주", "셋째주", "넷째주", "다섯째주"};

            int week = Integer.parseInt(weeklyBadge.substring(3, 4)) - 1;
            String weeklyInterviewKing = weeklyBadge.substring(0, 3) + weekKorean[week] + " 면접왕 " + ranking + "등";

            InterviewInfoResponseDto.Data n = InterviewInfoResponseDto.Data.builder()
                    .id(interviewById.getId())
                    .video(interviewService.getProfileImageUrl(interviewById.getVideoKey()))
                    .thumbnail(interviewService.getProfileImageUrl(interviewById.getThumbnailKey()))
                    .question(new QuestionResponseDto.data(interviewById.getQuestion().getId(),
                            interviewById.getQuestion().getCategory().name(),
                            interviewById.getQuestion().getContents(),
                            interviewById.getQuestion().getReference()))
                    .user(UserInfoResponseDto.UserBody.builder()
                            .githubLink(interviewById.getUser().getGithubLink())
                            .id(interviewById.getUser().getId())
                            .nickname(interviewById.getUser().getNickname())
                            .profileImageUrl(interviewService.getProfileImageUrl(interviewById.getUser().getProfileImageUrl()))
                            .introduce(interviewById.getUser().getIntroduce())
                            .build())
                    .badge(weeklyInterviewKing)
                    .note(interviewById.getMemo())
                    .scrapsMe(scrapMe)
                    .scrapsCount(interview.getScrapCount())
                    .commentsCount((long) commentsCount)
                    .likesCount(0L)
                    .isPublic(interviewById.getIsPublic())
                    .isMine(ismine)
                    .createdAt(interviewById.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .updatedAt(interviewById.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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

    @Async
    public void fixWeeklyInterviewRank() {
        List<WeeklyInterview> getInterviews = weeklyInterviewRepository.findByCreatedAtBetween(LocalDateTime.now().minus(1, ChronoUnit.WEEKS), LocalDateTime.now());

        int ranking = 0;
        for (WeeklyInterview weeklyInterview : getInterviews) {

            //인터뷰 뱃지 골드,실버,브론즈 저장
            String[] badge = {"Gold", "Silver", "Bronze", "NONE", "NONE"};
            Interview interviewById = interviewRepository.findById(weeklyInterview.getInterviewId())
                    .orElse(null);
            if (interviewById == null) {
                continue;
            }
            else if(interviewById.getUser().getIsDeleted()){
                continue;
            }
            ranking++;


            String weeklyBadge = weeklyInterview.getWeeklyBadge().substring(0, 7) + ranking + "등";
            log.info("CHANGE WEEKLY INTERVIEW >> {} (InterviewId: {})", weeklyBadge, weeklyInterview.getInterviewId());

            interviewById.updateBadge(badge[ranking - 1]);
            interviewRepository.save(interviewById);
            weeklyInterview.setWeeklyBadge(weeklyBadge);
        }
    }
}
