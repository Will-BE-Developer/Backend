package com.sparta.willbe.home.dto;

import com.sparta.willbe.batch.tables.BATCH_TopCategories;
import com.sparta.willbe.interview.dto.InterviewInfoResponseDto;
import com.sparta.willbe.question.dto.QuestionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeResponseDto {

    private List<InterviewInfoResponseDto.Data> latestInterviews;
    private List<InterviewInfoResponseDto.Data> weeklyInterviews;
    private List<QuestionResponseDto> TodaysQuestions ;
    private List<BATCH_TopCategories> topCategories;

}
