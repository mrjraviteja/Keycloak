package com.mrj.learningportal.service;

import java.util.List;
import java.util.Optional;

import com.mrj.learningportal.dto.UserResponseDto;
import com.mrj.learningportal.entity.UserEntity;

public interface UserService {
	public List<UserEntity> findAllUsers();
	public Optional<UserEntity> findUserById(Long id);
	public UserEntity addUser(UserEntity userEntity);
	public UserResponseDto userEntitytoDtoMapper(UserEntity userEntity);
	public void removeUserById(Long id);
}
