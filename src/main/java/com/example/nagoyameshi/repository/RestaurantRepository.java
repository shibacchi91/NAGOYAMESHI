package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	//キーワード検索
	public Page<Restaurant> findByNameLikeOrAddressLikeOrCategoryLikeOrderByCreatedAtDesc(String nameKeyword,
			String addressKeyword, String category, Pageable pageable);

	public Page<Restaurant> findByNameLikeOrAddressLikeOrCategoryLikeOrderByPriceAsc(String nameKeyword,
			String addressKeyword, String category, Pageable pageable);

	/*	public Page<Restaurant> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
	
		public Page<Restaurant> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);*/

	public Page<Restaurant> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

	public Page<Restaurant> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

	public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);

	public Page<Restaurant> findAllByOrderByPriceAsc(Pageable pageable);

	public List<Restaurant> findTop10ByOrderByCreatedAtDesc();

	/*	   // カテゴリで検索するメソッド
		public Page<Restaurant> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
		public Page<Restaurant> findByCategoryOrderByPriceAsc(String category, Pageable pageable);*/

	/*	@Query("SELECT DISTINCT r.category FROM Restaurant r")
		List<String> findAllCategories();*/
}
