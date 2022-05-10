package com.team7.project.scrap.repository;

import com.team7.project.scrap.model.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByUser_IdAndInterview_Id(Long userId, Long interviewId);
}