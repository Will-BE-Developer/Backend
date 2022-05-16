package com.team7.project.likes.repository;

import com.team7.project.likes.model.Likes;
import com.team7.project.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByInterviewId(Long interviewId) ;
}
