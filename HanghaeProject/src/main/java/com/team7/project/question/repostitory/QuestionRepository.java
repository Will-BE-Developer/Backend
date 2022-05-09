package com.team7.project.question.repostitory;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByCategory(CategoryEnum categoryEnum);
}