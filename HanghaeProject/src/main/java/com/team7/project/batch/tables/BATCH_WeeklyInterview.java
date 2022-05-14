package com.team7.project.batch.tables;

import com.team7.project._global.timestamped.model.Timestamped;
import com.team7.project.interview.model.Interview;
import com.team7.project.question.model.Question;
import com.team7.project.user.model.User;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class BATCH_WeeklyInterview extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    //삭제
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    //삭제
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "question_id")
//    private Question question;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Column(name = "scrap_count")
    private Long scrapCount;

    @Column(nullable = false)
    private String badge;


    public BATCH_WeeklyInterview(Interview interview){
//        this.user = interview.getUser();
//        this.question = interview.getQuestion();
        this.interview = interview;
    }

    public BATCH_WeeklyInterview(BATCH_WeeklyInterview weeklyInterviewTop3, String badge) {
//        this.user = weeklyInterviewTop3.getUser();
//        this.question = weeklyInterviewTop3.getQuestion();
        this.interview = weeklyInterviewTop3.getInterview();
        this.scrapCount = weeklyInterviewTop3.getScrapCount();
        this.badge = badge;
    }
}