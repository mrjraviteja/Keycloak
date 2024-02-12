package com.mrj.learningportal.dto;

import com.mrj.learningportal.entity.UserEntity;

import lombok.Data;

@Data
public class UserRequestDto {
	
	private String name;
	private UserEntity.Roles role;
}
