package com.sparta.willbe.scrap.model;

import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
public class Scrap extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    public Scrap(User user, Interview interview){
        this.user = user;
        this.interview = interview;
    }
}