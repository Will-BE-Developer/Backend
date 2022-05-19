package com.team7.project.batch.BATCH_repository;

import com.team7.project.batch.tables.BATCH_TopCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BATCH_TopCategoriesRepository extends JpaRepository<BATCH_TopCategories, Long> {

//   void deleteAllBatch(BATCH_TopCategories entity);
   BATCH_TopCategories save(BATCH_TopCategories batch_topCategories);
   List<BATCH_TopCategories> findAll();
}
