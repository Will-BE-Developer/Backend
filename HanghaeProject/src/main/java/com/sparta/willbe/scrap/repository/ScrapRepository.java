package com.sparta.willbe.scrap.repository;

import com.sparta.willbe.scrap.model.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByUser_IdAndInterview_Id(Long userId, Long interviewId);

    @Modifying
    @Query(value = "delete from scrap where interview_id = ?1 ", nativeQuery=true)
    void deleteByInterviewId(Long interviewId);

}