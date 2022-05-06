package com.team7.project.category.repository;

import com.team7.project.category.model.Category;
import com.team7.project.category.model.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(CategoryEnum categoryEnum);
}