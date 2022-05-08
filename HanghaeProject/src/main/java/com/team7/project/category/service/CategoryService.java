package com.team7.project.category.service;

//import com.team7.project.category.model.Category;
import com.team7.project.category.model.CategoryEnum;
//import com.team7.project.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.team7.project.category.model.CategoryEnum.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {
//    private final CategoryRepository categoryRepository;

    public List<CategoryEnum> getCategoryNames() {
        //        List<Category> categories = CategoryEnum
//
//        //      Refactoring needed
//        if(categories.isEmpty()){
//            initCategoryNames();
//            categories = categoryRepository.findAll();
//        }
//
//        for (Category category : categories) {
//            categoryNames.add(category.getCategoryName().name());
//        }

        return Arrays.asList(CategoryEnum.values());
    }

//    //      Refactoring needed
//    @Transactional
//    public void initCategoryNames() {
//        categoryRepository.save(new Category(DUMMY1));
//        categoryRepository.save(new Category(DUMMY2));
//        categoryRepository.save(new Category(DUMMY3));
//        categoryRepository.save(new Category(DUMMY4));
//        categoryRepository.save(new Category(DUMMY5));
//        categoryRepository.save(new Category(DUMMY6));
//        categoryRepository.save(new Category(DUMMY7));
//        categoryRepository.save(new Category(DUMMY8));
//        categoryRepository.save(new Category(DUMMY9));
//        categoryRepository.save(new Category(DUMMY10));
//        categoryRepository.save(new Category(DUMMY11));
//        categoryRepository.save(new Category(DUMMY12));
//    }


}
