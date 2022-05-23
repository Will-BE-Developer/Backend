package com.sparta.willbe.question.repostitory;

import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByCategory(CategoryEnum categoryEnum);
    List<Question> findAllByCategoryAndIsShow(CategoryEnum categoryEnum, boolean isShow);

}