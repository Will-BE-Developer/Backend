package com.sparta.willbe.comments.model;


import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.comments.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(exclude = {"interview","user"})
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
    @JoinColumn(name = "interview_id", updatable = false)
    private Interview interview;

    public Comment(CommentRequestDto requestDto, User user, Interview interview){
        this.contents = requestDto.getContents();
        this.rootId = requestDto.getRootId();
        this.rootName = requestDto.getRootName();
        this.user = user;
        this.interview = interview;
    }

    public void update(CommentRequestDto requestDto){
        this.contents = requestDto.getContents();
    }

}