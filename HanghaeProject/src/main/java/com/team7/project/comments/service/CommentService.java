package com.team7.project.comments.service;

import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.comments.model.Comment;
import com.team7.project.comments.repository.CommentRepository;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    //public void saveComment(CommentRequestDto requestDto, Long userId) {
    public Comment saveComment(CommentRequestDto requestDto, User user) {

        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다."));

        //rootname이 인터뷰면, interview repo에서 rootId(=interview id)로 interview를 찾고, 없으면 에러
        //rootname이 댓글이면, comment repo에서 rootId(=comment id)로 comment를 찾고,(없으면 에러), 그 코멘트의 인터뷰를 get
        Interview interview = new Interview();
        if (requestDto.getRootName().equals("interview")){
            interview = interviewRepository.getById(requestDto.getRootId());
        }

        Comment comment = new Comment(requestDto, user, interview);
        commentRepository.save(comment);
        return comment;
    }
}
