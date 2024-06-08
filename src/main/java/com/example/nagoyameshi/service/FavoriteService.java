package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FavoriteRegisterForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;



@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;

	public FavoriteService(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository,
			UserRepository userRepository) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void create(FavoriteRegisterForm favoriteRegisterForm, BindingResult bindingResult) {
		Restaurant restaurant = restaurantRepository.getReferenceById(favoriteRegisterForm.getRestaurantId());
		User user = userRepository.getReferenceById(favoriteRegisterForm.getUserId());

		// 重複チェック
		if (favoriteRepository.existsByRestaurantIdAndUserId(restaurant.getId(), user.getId())) {
			bindingResult.reject("duplicate", "This favorite already exists.");
			return;
		}

		Favorite favorite = new Favorite();
		favorite.setRestaurant(restaurant);
		favorite.setUser(user);

		favoriteRepository.save(favorite);
	}

	
	public boolean isFavorite(Restaurant restaurant, User user) {
		Favorite isFavorited = favoriteRepository.findByRestaurantAndUser(restaurant, user);
		return isFavorited != null;
	}
}
