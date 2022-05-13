package com.team7.project.weeklyInterview.model;

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
public class WeeklyInterview extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Column(name = "scrap_count")
    private Long scrapCount;  //1개씩 step하면서 순위??

    //public WeeklyInterview(Interview interview, Long scrapCount){
    public WeeklyInterview(Interview interview){
        this.user = interview.getUser();
        this.question = interview.getQuestion();
        this.interview = interview;
        //this.scrapCount = scrapCount;
    }

}