package com.sparta.willbe.batch.tables;

import com.sparta.willbe.category.model.CategoryEnum;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
public class BATCH_TopCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    public BATCH_TopCategories(CategoryEnum category){
        this.category = category;
    }

}
