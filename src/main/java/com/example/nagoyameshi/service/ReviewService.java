package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	

	public ReviewService(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository,
			UserRepository userRepository, RestaurantService restaurantService) {
		this.reviewRepository = reviewRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	
	}

	@Transactional
	public void create(ReviewRegisterForm reviewRegisterForm) {
		Review review = new Review();
		Restaurant restaurant = restaurantRepository.getReferenceById(reviewRegisterForm.getRestaurantId());
		User user = userRepository.getReferenceById(reviewRegisterForm.getUserId());
		review.setRestaurant(restaurant);
		review.setUser(user);
		review.setRating(reviewRegisterForm.getRating());
		review.setReview(reviewRegisterForm.getReview());

		reviewRepository.save(review);
	}

	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		review.setRating(reviewEditForm.getRating());
		review.setReview(reviewEditForm.getReview());
		reviewRepository.save(review);
	}

}
