package com.sparta.willbe.category.service;

//import com.team7.project.category.model.Category;
import com.sparta.willbe.category.model.CategoryEnum;
//import com.team7.project.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    public List<CategoryEnum> getCategoryNames() {

        return Arrays.asList(CategoryEnum.values());

    }

}
