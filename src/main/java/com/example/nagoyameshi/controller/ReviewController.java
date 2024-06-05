package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.RestaurantService;
import com.example.nagoyameshi.service.ReviewService;

//レビューコンテンツ
@Controller
@RequestMapping("/restaurants/{restaurantId}/review")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final RestaurantRepository restaurantRepository;

	public ReviewController(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository,
			ReviewService reviewService, RestaurantService restaurantService) {
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.restaurantRepository = restaurantRepository;

	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 6, sort = "id", direction = Direction.ASC) Pageable pageable) {

		Page<Review> reviewPage;
		reviewPage = reviewRepository.findAll(pageable);
		model.addAttribute("reviewPage", reviewPage);
		return "restaurants/show";
	}

	@GetMapping("/table")
	public String table(@PathVariable(name = "restaurantId") Integer restaurantId, Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
		Page<Review> reviewPage;
		reviewPage = reviewRepository.findAll(pageable);
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewPage", reviewPage);
		return "review/table";
	}

	@GetMapping("/register")
	public String register(@PathVariable(name = "restaurantId") Integer restaurantId, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
		return "review/register";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		if (bindingResult.hasErrors()) {
			return "restaurants/show/review/register";
		}
		User user = userDetailsImpl.getUser();
		reviewRegisterForm.setUserId(user.getId());
		reviewService.create(reviewRegisterForm);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");
		return "redirect:/restaurants/{restaurantId}";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable(name = "restaurantId") Integer restaurantId,
			Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		//User user = userDetailsImpl.getUser();
		Review review = reviewRepository.getReferenceById(id);
		ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), restaurant.getId(), review.getRating(),
				review.getReview());
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewEditForm", reviewEditForm);
		return "review/edit";
	}

	@PostMapping("{id}/update")
	public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult,
			@PathVariable(name = "restaurantId") Integer restaurantId, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "review/edit";
		}
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		model.addAttribute("restaurant", restaurant);
		reviewService.update(reviewEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		return "redirect:/restaurants/{restaurantId}";

	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		reviewRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		return "redirect:/restaurants/{restaurantId}";
	}

}