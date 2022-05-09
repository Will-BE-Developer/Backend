package com.team7.project.weeklyInterview.model;

import com.team7.project._global.timestamped.model.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity
public class WeeklyInterview extends Timestamped {
    // ID가 자동으로 생성 및 증가합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}