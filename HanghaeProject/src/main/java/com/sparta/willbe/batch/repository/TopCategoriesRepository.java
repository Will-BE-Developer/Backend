package com.sparta.willbe.batch.repository;

import com.sparta.willbe.batch.tables.BATCH_TopCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopCategoriesRepository extends JpaRepository<BATCH_TopCategories, Long> {

//   void deleteAllBatch(BATCH_TopCategories entity);
   BATCH_TopCategories save(BATCH_TopCategories batch_topCategories);
   List<BATCH_TopCategories> findAll();
}
