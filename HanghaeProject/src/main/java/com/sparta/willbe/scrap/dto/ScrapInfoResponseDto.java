package com.sparta.willbe.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrapInfoResponseDto {

    private ScrapInfoResponseDto.Data scrap;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Data{

        private Long interviewId;
        private Boolean scrapsMe;
        private Long scrapsCount;
        public void setScrapsCount(Long scrapsCount) {
            this.scrapsCount = scrapsCount;
        }
    }
}
