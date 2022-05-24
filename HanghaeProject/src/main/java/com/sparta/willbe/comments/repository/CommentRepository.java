package com.sparta.willbe.comments.repository;

import com.sparta.willbe.comments.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByInterviewId(Long interviewId);

    // 1개 인터뷰의 전체 댓글 조회
    @Query(value = "SELECT c FROM Comment c WHERE c.interview.id = ?1 and c.rootName = ?2 ")
    Page<Comment> findAllByInterviewIdAndRootName(Long interviewId, String rootName, Pageable pageable);

    @Query(value = "SELECT c FROM Comment c WHERE c.interview.id = ?1  and c.rootName = ?2")
    List<Comment> findAllNestedCommentInInterview(Long interviewId, String rootName);

    //1개 인터뷰의 댓글 총 갯수(대댓글 미포함)
    int countByInterview_IdAndRootName(Long interviewId, String rootName);

    //1개 인터뷰의 댓글 총 갯수(대댓글 포함)
    int countByInterview_Id(Long interviewId);

    @Query(value = "SELECT c.id FROM Comment c WHERE c.interview.id = ?1 and c.rootName = 'interview' ")
    List<Integer> rootCommentIdPerPage(Long interviewId, Pageable pageable);

    List<Comment> findAllByRootNameOrderByCreatedAtDesc(String rootname, Pageable pageable);

    List<Comment> findByRootIdAndRootName(Long rootId, String rootName);
}
