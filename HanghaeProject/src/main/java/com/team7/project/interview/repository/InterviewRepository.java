package com.team7.project.interview.repository;

import com.team7.project.interview.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
