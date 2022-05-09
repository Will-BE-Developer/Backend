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

    public List<CategoryEnum> getCategoryNames() {
        return Arrays.asList(CategoryEnum.values());
    }

}
