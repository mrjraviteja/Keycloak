package com.mrj.learningportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mrj.learningportal.entity.CategoryEntity;
import com.mrj.learningportal.entity.CourseEntity;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity,Long>{
	List<CourseEntity> findByCategoryEntity(CategoryEntity categoryEntity);
	CourseEntity findByAuthor(String author);
}
