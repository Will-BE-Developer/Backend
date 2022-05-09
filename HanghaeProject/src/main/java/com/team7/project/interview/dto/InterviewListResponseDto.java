package com.team7.project.interview.dto;

import com.team7.project._global.pagination.dto.PaginationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class InterviewListResponseDto {

    private List<InterviewResponseDto.Data> interviews;

    private PaginationResponseDto pagination;

}
