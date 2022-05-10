package com.team7.project.interview.repository;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.model.Interview;
import com.team7.project.question.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Page<Interview> findAllByIsDone(Boolean isDone, Pageable pageable);
    Page<Interview> findAllByIsDoneAndUser_Id(Boolean isDone, Long userId, Pageable pageable);
}