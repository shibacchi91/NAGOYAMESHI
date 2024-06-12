package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteService favoriteService;
	private final RestaurantService restaurantService;

	public RestaurantController(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,
			FavoriteService favoriteService, RestaurantService restaurantService) {
		this.restaurantRepository = restaurantRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteService = favoriteService;
		this.restaurantService = restaurantService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<Restaurant> restaurantPage;

		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc(
						"%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				restaurantPage = restaurantRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);

		return "restaurants/index";

	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model,
			@PageableDefault(page = 0, size = 6, sort = "id", direction = Direction.ASC) Pageable pageable,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		boolean isFavorite = false;
		Page<Review> reviewPage;

		// ユーザーがログインしているかを確認
		if (userDetailsImpl != null && userDetailsImpl.getUser() != null) {
			isFavorite = favoriteService.isFavorite(restaurant, userDetailsImpl.getUser());
			reviewPage = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);

			if (reviewPage.getTotalElements() == 0) {
				model.addAttribute("noReviewsMessage", "まだレビューがありません");
			} else {
				model.addAttribute("reviewPage", reviewPage);
			}

			User user = userDetailsImpl.getUser();
			ReservationInputForm reservationInputForm = new ReservationInputForm();
			Review review = reviewRepository.getReferenceById(id);
			ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm(restaurant.getId(), user.getId(),
					review.getRating(), review.getReview());

			model.addAttribute("reservationInputForm", reservationInputForm);
			model.addAttribute("review", review);
			model.addAttribute("user", user);
			model.addAttribute("reviewRegisterForm", reviewRegisterForm);
			model.addAttribute("isFavorite", isFavorite);
		} else {
			// 未ログインの場合でもレビューの表示を行う
			reviewPage = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);
			if (reviewPage.isEmpty()) {
				model.addAttribute("noReviewsMessage", "まだレビューがありません");
			} else {
				model.addAttribute("reviewPage", reviewPage);
			}
		}

		// ビューに渡すデータの設定
		model.addAttribute("restaurant", restaurant);

		// ログイン状態に応じて適切なテンプレートを返す
		return userDetailsImpl != null && userDetailsImpl.getUser() != null ? "restaurants/show"
				: "restaurants/notshow";
	}

	@GetMapping("/{restaurantId}/reviews/register")
	public String showReviewForm(@PathVariable Integer restaurantId, Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + restaurantId));
		model.addAttribute("restaurant", restaurant);

		if (userDetailsImpl != null && userDetailsImpl.getUser() != null) {
			User user = userDetailsImpl.getUser();
			ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm();
			reviewRegisterForm.setUserId(user.getId());
			reviewRegisterForm.setRestaurantId(restaurantId);
			model.addAttribute("reviewRegisterForm", reviewRegisterForm);
		}

		return "reviews/register"; // views/reviews/register.html というテンプレートが必要
	}

	@PostMapping("/{id}/delete")
	public String deleteRestaurant(@PathVariable Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		// レストラン削除処理
		restaurantService.deleteRestaurant(id);
		// 削除後のリダイレクト先を指定
		return "redirect:/restaurants";
	}
}