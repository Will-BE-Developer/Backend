//package com.team7.project.category.model;
//
//import com.team7.project.interview.model.Interview;
//import com.team7.project.question.model.Question;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter // get 함수를 일괄적으로 만들어줍니다.
//@NoArgsConstructor // 기본 생성자를 만들어줍니다.
//@Entity
//public class Category {
//    // ID가 자동으로 생성 및 증가합니다.
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    @Enumerated(EnumType.STRING)
//    private CategoryEnum categoryName;
//
//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
////    @Fetch(FetchMode.JOIN)
//    List<Question> questions = new ArrayList<>();
//
//    public Category(CategoryEnum categoryName){
//        this.categoryName = categoryName;
//    }
//
//    public void addQuestion(Question question){
//        questions.add(question);
//    }
//}