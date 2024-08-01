package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
	public List<Favorite> findByRestaurant(Restaurant Restaurant);

	public Page<Favorite> findByUser(User user, Pageable pageable);

	public Favorite findByRestaurantAndUser(Restaurant restaurant, User user);

	@Transactional
	public void deleteByRestaurantIdAndUserId(Integer restaurantId, Integer userId);

	@Transactional
	public Page<Favorite> findByUserId(Integer id, Pageable pageable);

	boolean existsByRestaurantIdAndUserId(Integer restaurantId, Integer userId);

	@Transactional
	void deleteByRestaurantId(Integer restaurantId);
	
	@Transactional
	void deleteByUserId(Integer userId);
}