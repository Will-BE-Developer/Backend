package com.sparta.willbe.batch.tables;


import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.question.model.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
public class TodayQuestion extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public TodayQuestion(Question question){
        this.question = question;
    }

}
