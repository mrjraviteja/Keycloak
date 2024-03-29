package com.mrj.learningportal.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mrj.learningportal.dto.UserResponseDto;
import com.mrj.learningportal.entity.CourseEntity;
import com.mrj.learningportal.entity.UserEntity;
import com.mrj.learningportal.repository.UserRepository;
import com.mrj.learningportal.service.CourseService;
import com.mrj.learningportal.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService{
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private CourseService courseService;
	private UserRepository userRepository;

	@Override
	public List<UserEntity> findAllUsers() {
		logger.info("@UserServiceImpl - Fetching all users.");
		return userRepository.findAll();
	}

	@Override
	public Optional<UserEntity> findUserById(Long id) {
		logger.info("@UserServiceImpl - Fetching user by id.");
		return userRepository.findById(id);
	}
	
	@Override
	public UserEntity addUser(UserEntity userEntity)
	{
		logger.info("@UserServiceImpl - Adding user to UserRepository.");
		return userRepository.save(userEntity);
	}

	@Override
	public UserResponseDto userEntitytoDtoMapper(UserEntity userEntity) {
		logger.info("@UserServiceImpl - UserEntity to UserResponseDTO Mapper.");
		UserResponseDto userDto = new UserResponseDto();
	    userDto.setId(userEntity.getId());
	    userDto.setName(userEntity.getName());
	    userDto.setRole(userEntity.getRole());
	    if(userEntity.getEnrolledCourses() != null)
	    {
	    	userDto.setEnrolledCourses(userEntity.getEnrolledCourses().stream().map(registration -> courseService.mapCourseEntitytoCourseDto(registration.getCourseEntity())).toList());
	    }
	    if(userEntity.getFavouriteCourses() != null)
	    {
	    	List<CourseEntity> courses = userEntity.getFavouriteCourses().stream().map(pred -> pred.getCourseFavEntity()).toList();
	    	userDto.setFavoriteCourses(courses.stream().map(courseService::mapCourseEntitytoCourseDto).toList());
	    }
	    return userDto;
	}

	@Override
	public void removeUserById(Long id) {
		logger.info("@UserServiceImpl - Deleting user by id.");
		userRepository.deleteById(id);
		logger.info("@UserServiceImpl - User deleted.");
	}
	
}
