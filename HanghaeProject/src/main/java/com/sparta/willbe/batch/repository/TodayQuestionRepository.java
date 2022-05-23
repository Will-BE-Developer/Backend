package com.sparta.willbe.batch.repository;


import com.sparta.willbe.batch.tables.BATCH_TodayQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodayQuestionRepository extends JpaRepository<BATCH_TodayQuestion, Long> {
    BATCH_TodayQuestion save(BATCH_TodayQuestion batch_todayQuestion);
    void deleteAll();
    List<BATCH_TodayQuestion> findAll();
}
