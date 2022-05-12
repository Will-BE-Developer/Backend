package com.team7.project.batch.BATCH_repository;


import com.team7.project.batch.tables.BATCH_TodayQuestion;
import com.team7.project.batch.tables.BATCH_TopCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BATCH_TodayQuestionRepository extends JpaRepository<BATCH_TodayQuestion, Long> {
    BATCH_TodayQuestion save(BATCH_TodayQuestion batch_todayQuestion);
    void deleteAll();
}
