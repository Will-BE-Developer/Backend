package com.sparta.willbe.batch.tables;

import com.sparta.willbe.interview.model.Interview;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name="batch_weekly_interview")
public class BATCH_WeeklyInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    //@OneToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="interview_id")
    private Interview interview;

    @Column(name = "scrap_count")
    private Long scrapCount;

    @Column(nullable = false)
    private String badge;

    @Column(name = "weekly_badge")
    private String weeklyBadge;

    public BATCH_WeeklyInterview(BATCH_WeeklyInterview weeklyInterviewTop3, String badge, String weeklyBadge) {
        this.interview = weeklyInterviewTop3.getInterview();
        this.scrapCount = weeklyInterviewTop3.getScrapCount();
        this.badge = badge;
        this.weeklyBadge = weeklyBadge;
    }

    public BATCH_WeeklyInterview(Interview interview, Long ScrapCount, String badge, String weeklyBadge) {
        this.interview = interview;
        this.scrapCount = ScrapCount;
        this.badge = badge;
        this.weeklyBadge = weeklyBadge;
    }

}