package com.team7.project.question.model;

import com.team7.project._timestamped.model.Timestamped;
import com.team7.project.category.model.Category;
import com.team7.project.interview.model.Interview;
import com.team7.project.scrap.model.Scrap;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity
public class Question extends Timestamped {
    // ID가 자동으로 생성 및 증가합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private Boolean isShow;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Fetch(FetchMode.JOIN)
    List<Interview> interviews = new ArrayList<>();

}