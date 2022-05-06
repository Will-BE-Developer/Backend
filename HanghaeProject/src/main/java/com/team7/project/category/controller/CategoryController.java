package com.team7.project.category.controller;

import com.team7.project.category.dto.CategoryResponseDto;
import com.team7.project.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    public CategoryResponseDto getAllCategories() {
        return new CategoryResponseDto(categoryService.getCategoryNames());
    }
}
