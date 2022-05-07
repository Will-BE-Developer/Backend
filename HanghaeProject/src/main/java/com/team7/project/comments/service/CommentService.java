package com.team7.project.comments.service;

import com.team7.project.advice.RestException;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.repository.CommentRepository;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            InterviewRepository interviewRepository){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }

    public Comment saveComment(CommentRequestDto requestDto, User user) {

        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다."));

        //rootname이 인터뷰면, interview repo에서 rootId(=interview id)로 interview를 찾고, 없으면 에러
        //rootname이 댓글이면, comment repo에서 rootId(=comment id)로 comment를 찾고, 없으면 에러, 그 코멘트의 인터뷰를 get
        Comment comment = new Comment();
        if (requestDto.getRootName().equals("interview")){
            Interview interview = interviewRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 인터뷰는 존재하지 않습니다.")
            );
            comment = new Comment(requestDto, user, interview.getId());
            commentRepository.save(comment);
        }else if(requestDto.getRootName().equals("comment")){
            Comment rootComment = commentRepository.findById(requestDto.getRootId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
            );
            Interview interview = rootComment.getInterview();
            comment = new Comment(requestDto, user, interview.getId());
            commentRepository.save(comment);
        }
        return comment;
    }

    @Transactional
    public Comment editComment(Long commentId, CommentRequestDto requestDto, User user) {
        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다."));

        //수정하려는 댓글 id가 존재하는지
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
        );

        //기존에 rootName과 일치하는지
        if (comment.getRootName().equals(requestDto.getRootName())){
            //기존 rootId와 일치하는지
            if (comment.getRootId().equals(requestDto.getRootId())){
                comment.update(requestDto);
            }else{
                throw new RestException(HttpStatus.BAD_REQUEST, "rootId가 일치하지 않습니다.");
            }
        }else{
            throw new RestException(HttpStatus.BAD_REQUEST, "rootName이 일치하지 않습니다.");
        }

        return comment;
    }

    @Transactional
    public Comment deleteComment(Long commentId) {
        //삭제 전 response를 위해 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다.")
        );
        commentRepository.deleteById(comment.getId());

        return comment;
    }

    public List<Comment> getListOfCommentOfInterview(Long interviewId) { //getCommentList
        //List<Comment> commentList = commentRepository.findAllByInterviewId(interviewId);
        List<Comment> commentList = commentRepository.findAllByInterviewIdAndRootName(interviewId, "interview");

        return commentList;
    }
    public List<Comment> getListOfCommentOfComment(Long interviewId) {
        List<Comment> commentList = commentRepository.findAllByInterviewIdAndRootName(interviewId, "comment");

        return commentList;
    }
}
