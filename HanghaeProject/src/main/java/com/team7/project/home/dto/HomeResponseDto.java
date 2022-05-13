package com.team7.project.home.dto;

import com.team7.project.batch.tables.BATCH_TopCategories;
import com.team7.project.comments.dto.CommentResponseDto;
import com.team7.project.interview.dto.InterviewInfoResponseDto;
import com.team7.project.question.dto.QuestionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeResponseDto {

    private List<CommentResponseDto.ResponseComment> latestCommnets;
//    private List<InterviewInfoResponseDto.Data> weeklyInterviews;
    private List<QuestionResponseDto> TodaysQuestions ;
    private List<BATCH_TopCategories> topCategories;

}
