package com.team7.project.comments.model;


import com.team7.project._timestamped.model.Timestamped;
import com.team7.project.interview.model.Interview;
import com.team7.project.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity
public class Comment extends Timestamped {
    // ID가 자동으로 생성 및 증가합니다.
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

}