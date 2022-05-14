package com.team7.project.batch.tables;

import com.team7.project._global.timestamped.model.Timestamped;
import com.team7.project.interview.model.Interview;
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

    // User, Question 삭제

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Column(name = "scrap_count")
    private Long scrapCount;

    @Column(nullable = false)
    private String badge;

    @Column(name = "weekly_badge")
    //@JsonInclude(JsonInclude.Include.NON_NULL)  //null은 제외한다.
    //@Transient
    private String weeklyBadge;
    //private List<String> weeklyBadge = new ArrayList<>();  //basic attribute type should not be a container

    public BATCH_WeeklyInterview(Interview interview){
        this.interview = interview;
    }

    public BATCH_WeeklyInterview(BATCH_WeeklyInterview weeklyInterviewTop3, String badge) {
        this.interview = weeklyInterviewTop3.getInterview();
        this.scrapCount = weeklyInterviewTop3.getScrapCount();
        this.badge = badge;
    }

    public BATCH_WeeklyInterview(BATCH_WeeklyInterview weeklyInterviewTop3, String badge, String weeklyBadge) {
        this.interview = weeklyInterviewTop3.getInterview();
        this.scrapCount = weeklyInterviewTop3.getScrapCount();
        this.badge = badge;
        this.weeklyBadge = weeklyBadge;
    }

    public void writeWeeklyBadge(String weeklyBadge){
        this.weeklyBadge = weeklyBadge;
    }

//    @Transient //해당 메서드 영속 제외 대상
//    public String getWeeklyBadge() {
//        return weeklyBadge;
//    }



}