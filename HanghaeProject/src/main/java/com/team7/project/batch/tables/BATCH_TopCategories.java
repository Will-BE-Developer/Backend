package com.team7.project.batch.tables;

import com.team7.project.category.model.CategoryEnum;
import lombok.*;
import org.springframework.context.annotation.Bean;

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
