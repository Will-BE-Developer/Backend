package com.sparta.willbe.batch.tables;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
public class BATCH_TodayQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long questionId;

    public BATCH_TodayQuestion(Long questionId){
        this.questionId = questionId;
    }
}
