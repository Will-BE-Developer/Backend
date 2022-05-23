package com.sparta.willbe._global.timestamped.model;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // Entity가 자동으로 컬럼으로 인식합니다.
@EntityListeners(AuditingEntityListener.class) // 생성/변경 시간을 자동으로 업데이트합니다.
public abstract class Timestamped {

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime modifiedAt;
}