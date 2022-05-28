package com.sparta.willbe.category.dto;

import com.sparta.willbe.category.model.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {

    private List<CategoryEnum> categories;

}
