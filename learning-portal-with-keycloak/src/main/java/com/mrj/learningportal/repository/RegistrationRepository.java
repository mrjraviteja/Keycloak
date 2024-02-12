package com.mrj.learningportal.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mrj.learningportal.entity.CourseEntity;
import com.mrj.learningportal.entity.RegistrationEntity;
import com.mrj.learningportal.entity.UserEntity;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long>{
	public RegistrationEntity findByUserEntityAndCourseEntity(UserEntity userEntity,CourseEntity courseEntity);
	public List<RegistrationEntity> findByUserEntity(UserEntity userEntity);
	public List<RegistrationEntity> findByCourseEntity(CourseEntity courseEntity);
}
