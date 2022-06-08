package com.sparta.willbe.likes.model;

import com.sparta.willbe.interview.model.Interview;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Likes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="interview_id")
    private Interview interview;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<Integer, Integer> likesData = new HashMap<>();

    @Builder
    public Likes(Long id, Interview interview,Map<Integer, Integer> likesData){
        this.id = id;
        this.interview = interview;
        this.likesData = likesData;
    }
}


