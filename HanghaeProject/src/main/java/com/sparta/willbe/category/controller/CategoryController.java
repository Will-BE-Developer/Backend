package com.sparta.willbe.category.controller;

import com.sparta.willbe.category.dto.CategoryResponseDto;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.category.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    @ApiOperation(value = "카테고리 목록 불러오기")
    public ResponseEntity<CategoryResponseDto> getAllCategories() {

        log.info("READ ALL CATEGORY LIST");
        List<CategoryEnum> categoryEnumList = categoryService.getCategoryNames();
        CategoryResponseDto body = new CategoryResponseDto(categoryEnumList);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
