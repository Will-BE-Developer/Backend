package com.sparta.willbe.likes.repository;

import com.sparta.willbe.likes.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByInterviewId(Long interviewId) ;
}
