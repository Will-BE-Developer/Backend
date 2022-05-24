package com.sparta.willbe.batch.repository;


import com.sparta.willbe.batch.tables.TodayQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodayQuestionRepository extends JpaRepository<TodayQuestion, Long> {
    TodayQuestion save(TodayQuestion batch_todayQuestion);
    void deleteAll();
    List<TodayQuestion> findAll();
    List<TodayQuestion> findTop3ByOrderByCreatedAtDesc();
}