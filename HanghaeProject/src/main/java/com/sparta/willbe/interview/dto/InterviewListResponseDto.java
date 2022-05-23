package com.sparta.willbe.interview.dto;

import com.sparta.willbe._global.pagination.dto.PaginationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class InterviewListResponseDto {

    private List<InterviewInfoResponseDto.Data> interviews;

    private PaginationResponseDto pagination;

}
