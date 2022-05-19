package com.team7.project.likes.dto;

import com.team7.project.user.dto.UserInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class LikesResponseDto {
    private Map<Integer,Integer> likesData ;
    private Long TopOne;
    private Long TopTwo;
    private Long TopThree;
    private int totalCount;
    private UserInfoResponseDto.UserBody userInfoResponseDto;

    @Builder
    public LikesResponseDto(Map<Integer,Integer> likesData,
                            Long TopOne,
                            Long TopTwo,
                            Long TopThree,
                            int totalCount,UserInfoResponseDto.UserBody userInfoResponseDto){
        this.likesData = likesData;
        this.TopOne = TopOne;
        this.TopTwo = TopTwo;
        this.TopThree = TopThree;
        this.totalCount = totalCount;
        this.userInfoResponseDto = userInfoResponseDto;
    }
}
