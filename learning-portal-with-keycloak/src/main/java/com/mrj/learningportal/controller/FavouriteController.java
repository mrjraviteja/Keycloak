package com.mrj.learningportal.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrj.learningportal.dto.FavouriteResponseDto;
import com.mrj.learningportal.entity.FavouriteEntity;
import com.mrj.learningportal.service.FavouriteService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/favourites")
public class FavouriteController {
	private static final Logger logger = LoggerFactory.getLogger(FavouriteController.class);
	
	private FavouriteService favouriteService;
	
	// Accessed only by author
	@GetMapping
	public ResponseEntity<Object> showAllFavourites()
	{
		logger.info("@FavouriteController - Fetching all favourites.");
		List<FavouriteEntity> favourites =  favouriteService.findAllFavourites();
		if(favourites.isEmpty())
		{
			logger.info("@CategoryController - Failed to fetch favourites.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No favourites found!");
		}
		List<FavouriteResponseDto> favouritesresp = favourites.stream().map(favouriteService::mapFavouriteEntitytoDto).toList();
		return ResponseEntity.status(HttpStatus.OK).body(favouritesresp);
	}
}
