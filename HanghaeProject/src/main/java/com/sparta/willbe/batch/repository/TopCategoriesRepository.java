package com.sparta.willbe.batch.repository;

import com.sparta.willbe.batch.tables.TopCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopCategoriesRepository extends JpaRepository<TopCategories, Long> {

   TopCategories save(TopCategories batch_topCategories);
   List<TopCategories> findTop6ByOrderByCreatedAtDesc();
}

