package com.team7.project.comments.repository;

import com.team7.project.comments.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByInterviewId(Long interviewId);

    @Query(value = "SELECT * FROM comment WHERE interview_id = :interviewId  and root_name = :rootName", nativeQuery = true)
    List<Comment> findAllByInterviewIdAndRootName(@Param("interviewId") Long interviewId, @Param("rootName") String rootName);
}

