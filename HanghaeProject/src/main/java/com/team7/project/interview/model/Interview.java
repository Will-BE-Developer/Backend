package com.team7.project.interview.model;

import com.team7.project._timestamped.model.Timestamped;
import com.team7.project.comments.model.Comment;
import com.team7.project.question.model.Question;
import com.team7.project.scrap.model.Scrap;
import com.team7.project.user.model.User;
import com.team7.project.weeklyInterview.model.WeeklyInterview;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity
public class Interview extends Timestamped {
    // ID가 자동으로 생성 및 증가합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String videoKey;

    @Column(nullable = false, unique = true)
    private String thumbnailKey;

    @Column(nullable = true, length = 1000)
    private String memo;

    @Column(nullable = false)
    private String isPublic;

    @Column(nullable = false)
    private String badge;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Fetch(FetchMode.JOIN)
    List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Fetch(FetchMode.JOIN)
    List<Comment> comments = new ArrayList<>();


//   private WeeklyInterview weeklyInterview;

}
