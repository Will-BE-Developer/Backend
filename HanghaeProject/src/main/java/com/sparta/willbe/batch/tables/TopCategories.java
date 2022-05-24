package com.sparta.willbe.batch.tables;

import com.sparta.willbe._global.timestamped.model.Timestamped;
import com.sparta.willbe.category.model.CategoryEnum;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
public class TopCategories extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    public TopCategories(CategoryEnum category){
        this.category = category;
    }

}
