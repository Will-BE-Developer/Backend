package com.team7.project.comments.model;


import com.team7.project._timestamped.model.Timestamped;
import com.team7.project.comments.dto.CommentRequestDto;
import com.team7.project.interview.model.Interview;
import com.team7.project.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private Long rootId;

    @Column(nullable = false)
    private String rootName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    //public Comment(CommentRequestDto requestDto, Long userId){
    //public Comment(CommentRequestDto requestDto, User user, Interview interview){
    //public Comment(CommentRequestDto requestDto, Long userId, Long interviewId){
    public Comment(CommentRequestDto requestDto, User user, Long interviewId){
        this.contents = requestDto.getContents();
        this.rootId = requestDto.getRootId();
        this.rootName = requestDto.getRootName();
        //this.user = new User();
        //this.user.setId(userId);
        this.user = user;
        //this.interview = interview;
        this.interview = new Interview();
        this.interview.setId(interviewId);
    }

}