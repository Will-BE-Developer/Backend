package com.team7.project.interview.repository;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.model.Interview;
import com.team7.project.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Question> findAllByIsDoneOrderByCreatedAt(Boolean isDone);
}