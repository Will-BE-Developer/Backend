package com.sparta.willbe.scrap.model;

import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity
public class Scrap extends Timestamped {
    // ID가 자동으로 생성 및 증가합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    public Scrap(User user, Interview interview){
        this.user = user;
        this.interview = interview;
    }
}