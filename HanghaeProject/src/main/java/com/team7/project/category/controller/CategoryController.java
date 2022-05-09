package com.team7.project.category.controller;

import com.team7.project.category.dto.CategoryResponseDto;
import com.team7.project.category.model.CategoryEnum;
import com.team7.project.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    public ResponseEntity<CategoryResponseDto> getAllCategories() {
        List<CategoryEnum> categoryEnumList = categoryService.getCategoryNames();

        return new ResponseEntity<>(new CategoryResponseDto(categoryEnumList), HttpStatus.OK);
    }
}
