package com.sparta.willbe.batch.BATCH_repository;

import com.sparta.willbe.batch.tables.BATCH_WeeklyInterview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BATCH_WeeklyInterviewRepository extends JpaRepository<BATCH_WeeklyInterview, Long> {

    /** reverse('null') as weekly_badge
     *  Field 'weekly_badge' doesn't have a default value에러 때문에 가상의 컬럼 추가함
     * */
    @Query(value = "SELECT *, reverse('null') as weekly_badge FROM interview i " +
            "inner JOIN ( " +
            "SELECT interview_id, count(interview_id) as scrap_count FROM scrap " +
            "WHERE created_at BETWEEN DATE_ADD(NOW(), INTERVAL -1 WEEK ) AND NOW() " +
            "group by interview_id " +
            "order by count(interview_id) DESC LIMIT 10 " +
            ") s " +
            "ON i.id IN (s.interview_id) " +
            "WHERE i.is_public = 1 order by scrap_count DESC, created_at DESC ", nativeQuery = true)

    List<BATCH_WeeklyInterview> findWeeklyInterview(Pageable pageable);

    BATCH_WeeklyInterview findByWeeklyBadge(String lowRank);

    BATCH_WeeklyInterview findByInterviewId(Long interviewId);
}
