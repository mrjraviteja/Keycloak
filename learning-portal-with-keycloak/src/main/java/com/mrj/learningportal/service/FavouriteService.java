package com.mrj.learningportal.service;

import java.util.List;
import java.util.Optional;

import com.mrj.learningportal.dto.FavouriteResponseDto;
import com.mrj.learningportal.entity.CourseEntity;
import com.mrj.learningportal.entity.FavouriteEntity;
import com.mrj.learningportal.entity.UserEntity;

public interface FavouriteService {
	public List<FavouriteEntity> findAllFavourites();
	public Optional<FavouriteEntity> findFavouriteById(Long id);
	public void saveFavourite(FavouriteEntity favouriteEntity);
	public List<FavouriteEntity> findFavouriteByUserEntity(UserEntity userEntity);
	public List<FavouriteEntity> findFavouriteByCourseEntity(CourseEntity courseEntity);
	public FavouriteEntity getFavouriteByUserAndCourse(UserEntity userEntity,CourseEntity courseEntity);
	public boolean checkFavouriteByUserAndCourse(UserEntity userEntity,CourseEntity courseEntity);
	public void removeFavourite(FavouriteEntity favouriteEntity);
	public FavouriteResponseDto mapFavouriteEntitytoDto(FavouriteEntity favouriteEntity);
}
