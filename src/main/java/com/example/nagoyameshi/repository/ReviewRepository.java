package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;


public interface ReviewRepository extends JpaRepository<Review, Integer>{

	Review getReferenceById(Review review);
	List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
	
	public Page<Review> findByUserOrderByCreatedAtDesc(User user,Pageable pageable);

	public Page<Review> findByRestaurantOrderByCreatedAtDesc(Restaurant resutaurant, Pageable pageable);

	
}
