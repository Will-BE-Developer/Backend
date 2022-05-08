package com.team7.project.interview.dto;

import com.team7.project._pagination.dto.PaginationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class InterviewListResponse {
    private List<InterviewResponse.Data> interviews;
    private PaginationResponseDto pagination;

}
