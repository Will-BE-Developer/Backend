package com.team7.project.batch.BATCH_repository;

import com.team7.project.batch.tables.BATCH_WeeklyInterview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BATCH_WeeklyInterviewRepository extends JpaRepository<BATCH_WeeklyInterview, Long> {

    @Query(value = "SELECT * FROM interview i " +
            "inner JOIN ( " +
            "SELECT interview_id, count(interview_id) as scrap_count FROM scrap " +
            "WHERE created_at BETWEEN DATE_ADD(NOW(), INTERVAL -1 WEEK ) AND NOW() " +
            "group by interview_id " +
            "order by count(interview_id) DESC LIMIT 3 " +
            ") s " +
            "ON i.id IN (s.interview_id)", nativeQuery = true)

        //List<Interview> findWeeklyInterview();
        //Page<Interview> findWeeklyInterview(Pageable pageable);
    List<BATCH_WeeklyInterview> findWeeklyInterview(Pageable pageable);
}
