package com.team7.project.comments.repository;

import com.team7.project.comments.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByInterviewId(Long interviewId);

    @Query(value = "SELECT * FROM comment WHERE interview_id = :interviewId  and root_name = :rootName LIMIT :per OFFSET :offset", nativeQuery = true)
    //List<Comment> findAllByInterviewIdAndRootName(@Param("interviewId") Long interviewId, @Param("rootName") String rootName);
    List<Comment> findAllByInterviewIdAndRootName(@Param("interviewId") Long interviewId, @Param("rootName") String rootName,
                                                  @Param("per") int per,  @Param("offset") int offset);
    //Page<Comment> findAllByInterviewIdAndRootName(@Param("interviewId") Long interviewId, @Param("rootName") String rootName, Pageable pageable); //추후 적용 테스트

    @Query(value = "SELECT * FROM comment WHERE interview_id = :interviewId  and root_name = :rootName", nativeQuery = true)
    List<Comment> findAllByInterviewIdAndRootNameNest(@Param("interviewId") Long interviewId, @Param("rootName") String rootName);

    //Long countByInterview_Id(Long interviewId); //대댓글 포함
    Long countByInterview_IdAndRootName(Long interviewId, String rootName);

    List<Comment> findAllByRootNameOrderByCreatedAtDesc(String rootname, Pageable pageable);
}

