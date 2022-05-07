package com.team7.project.comments.repository;

import com.team7.project.comments.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByInterviewId(Long interviewId);
}
