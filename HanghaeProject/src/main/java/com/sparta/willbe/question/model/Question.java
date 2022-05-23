package com.sparta.willbe.question.model;

import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.category.model.CategoryEnum;
import com.sparta.willbe.interview.model.Interview;
//import com.team7.project.category.model.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Interview> interviews = new ArrayList<>();

    public Question(String contents, String reference, CategoryEnum category){
        this.contents = contents;
        this.reference = reference;
        this.isShow = false;
        this.category = category;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }
}