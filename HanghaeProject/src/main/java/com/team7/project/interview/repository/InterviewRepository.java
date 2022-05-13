package com.team7.project.interview.repository;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.model.Interview;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    //인터뷰 전체보기
    Page<Interview> findAllByIsDoneAndIsPublic(Boolean isDone, Boolean isPublic, Pageable pageable);
    Page<Interview> findAllByIsDoneAndIsPublicAndQuestion_Category(Boolean isDone, Boolean isPublic, CategoryEnum categoryEnum, Pageable pageable);

    @Query(value = "select p from Interview p where p.isDone = true and p.isPublic = true Order By size(p.scraps) desc")
    Page<Interview> findAllOrderByScrapsCountDesc(Pageable pageable);

    @Query(value = "select p from Interview p where p.isDone = true and p.isPublic = true and p.question.category = ?1 Order By size(p.scraps) desc")
    Page<Interview> findAllByQuestion_CategoryOrderByScrapsCountDesc(CategoryEnum categoryEnum, Pageable pageable);

    Page<Interview> findAllByIsDoneAndUser_Id(Boolean isDone, Long userId, Pageable pageable);

    Page<Interview> findAllByIsDoneAndScraps_User_Id(Boolean isDone, Long userId, Pageable pageable);

    //주간 면접왕
    @Query(value = "SELECT i.id as interview_id, i.user_id, i.question_id, s.scrap_count FROM interview i " +
                        "inner JOIN ( " +
                            "SELECT interview_id, count(interview_id) as scrap_count FROM scrap " +
                            "WHERE created_at BETWEEN DATE_ADD(NOW(), INTERVAL -1 WEEK ) AND NOW() " +
                            "group by interview_id " +
                            "order by count(interview_id) DESC LIMIT 3 " +
                        ") s " +
                        "ON i.id IN (s.interview_id)", nativeQuery = true)
    //List<Interview> findWeeklyInterview();
    Page<Interview> findWeeklyInterview(Pageable pageable);
    //List<Interview> findWeeklyInterview(Pageable pageable);
}