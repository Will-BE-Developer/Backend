package com.team7.project.interview.repository;

import com.team7.project.category.model.CategoryEnum;
import com.team7.project.interview.model.Interview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;


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

    @Query("SELECT q.category FROM Interview i LEFT JOIN i.question q WHERE i.question.id = q.id GROUP BY q.category ORDER BY COUNT(q.category ) DESC ")
    List<CategoryEnum> findCategoriesOrderedByCategoryCount(Pageable pageable);
}