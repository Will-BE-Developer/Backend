package com.sparta.willbe.batch.tables;

import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.interview.model.Interview;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name="weekly_interview")
public class WeeklyInterview extends Timestamped {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    //@OneToOne(fetch = FetchType.LAZY)
    //@ManyToOne(fetch = FetchType.LAZY)
    //@ManyToOne(cascade = CascadeType.ALL)
    //@ManyToOne(optional = false)  //inner join
    @ManyToOne
    @JoinColumn(name="interview_id")
    //@Column(name="interview_id")
    private Interview interview;

    @Column(name = "scrap_count")
    private Long scrapCount;

    @Column(nullable = false)
    private String badge;

    @Column(name = "weekly_badge")
    private String weeklyBadge;

    public WeeklyInterview(WeeklyInterview weeklyInterviewTop3, String badge, String weeklyBadge) {
        this.interview = weeklyInterviewTop3.getInterview();
        this.scrapCount = weeklyInterviewTop3.getScrapCount();
        this.badge = badge;
        this.weeklyBadge = weeklyBadge;
    }

    public WeeklyInterview(Interview interview, Long ScrapCount, String badge, String weeklyBadge) {
        this.interview = interview;
        this.scrapCount = ScrapCount;
        this.badge = badge;
        this.weeklyBadge = weeklyBadge;
    }

}