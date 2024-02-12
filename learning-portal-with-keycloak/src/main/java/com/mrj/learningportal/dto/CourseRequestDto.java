package com.mrj.learningportal.dto;

import lombok.Data;

@Data
public class CourseRequestDto {
	private String name;
	private String author;
	private String desc;
	private String category;
}
