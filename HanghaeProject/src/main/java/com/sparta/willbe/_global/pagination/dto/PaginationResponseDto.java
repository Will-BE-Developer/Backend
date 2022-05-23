package com.sparta.willbe._global.pagination.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginationResponseDto {
    // default value is 8
    private Long per = 8L;
    // total length of variable
    private Long totalCounts;
    // totalPages = (Long) Math.ceil((double) totalCounts / per)
    private Long totalPages;
    // default value is 1
    private Long currentPage = 1L;
//    pass nextPage in builder to make null
    private Long nextPage;
    private Boolean isLastPage = false;

    public PaginationResponseDto(Long per, Long totalCounts, Long currentPage){
        if(per > 0){
            this.per = per;
        }
        this.totalCounts = totalCounts;
        this.totalPages = (long) Math.ceil((double) this.totalCounts / this.per);
        if(currentPage > 0){
            this.currentPage = currentPage;
        }
        if(this.totalPages > this.currentPage){
            this.nextPage = this.currentPage + 1;
        }
        else {
            this.isLastPage = true;
        }
    }
}
