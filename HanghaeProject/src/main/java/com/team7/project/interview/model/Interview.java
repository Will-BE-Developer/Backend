package com.team7.project.interview.model;

import com.team7.project._global.timestamped.model.Timestamped;
import com.team7.project.comments.model.Comment;
import com.team7.project.question.model.Question;
import com.team7.project.scrap.model.Scrap;
import com.team7.project.user.model.User;
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
    private Boolean isPublic;

    @Column(nullable = false)
    private Boolean isDone;

    @Column(nullable = false)
    private String badge;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();


//    making draft
    public Interview(String videoKey, String thumbnailKey){
        this.videoKey = videoKey;
        this.thumbnailKey = thumbnailKey;
        this.memo = "";
        this.isPublic = false;
        this.isDone = false;
        this.badge = "NONE";
    }

    // complete interview
    public Interview complete(String memo, Boolean isPublic, User user, Question question){
        this.memo = memo;
        this.isPublic = isPublic;
        this.user = user;
        this.question = question;
        this.isDone = true;
        return this;
    }

    //  update interview (only memo, isPublic)
    public Interview update(String memo, Boolean isPublic){
        this.memo = memo;
        this.isPublic = isPublic;
        return this;
    }




//   private WeeklyInterview weeklyInterview;

}
