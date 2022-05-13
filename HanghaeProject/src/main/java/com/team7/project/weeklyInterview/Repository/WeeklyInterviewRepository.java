package com.team7.project.weeklyInterview.Repository;

import com.team7.project.weeklyInterview.model.WeeklyInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyInterviewRepository extends JpaRepository<WeeklyInterview, Long> {


}
