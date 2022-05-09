package com.team7.project.category.dto;

import com.team7.project.category.model.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {

    private List<CategoryEnum> categories;

}
