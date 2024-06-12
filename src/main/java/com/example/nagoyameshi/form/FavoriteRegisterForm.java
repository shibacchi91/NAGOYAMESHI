package com.example.nagoyameshi.form;

import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRegisterForm {
	@NotNull
	private Integer id;
	
	@NotNull
	private Integer restaurantId;

	@NotNull
	private Integer userId;

	@Transactional
	public void deleteByRestaurantIdAndUserId(Integer restaurantId, Integer userId) {
		this.restaurantId = restaurantId;
		this.userId = userId;
	}
}