package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FavoriteRegisterForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

	private final FavoriteRepository favoriteRepository;
	private final FavoriteService favoriteService;
	private final RestaurantRepository restaurantRepository;

	public FavoriteController(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository,
			FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.favoriteService = favoriteService;
		this.restaurantRepository = restaurantRepository;
	}

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model, HttpSession httpSession) {
		User user = userDetailsImpl.getUser();
		Page<Favorite> favoritePage = favoriteRepository.findByUserId(user.getId(), pageable);
		httpSession.setAttribute("favoriteHouse", restaurantRepository.findAll());

		model.addAttribute("user", user);
		model.addAttribute("favoritePage", favoritePage);

		return "favorites/index";
	}

	@PostMapping("/{restaurantId}/create")
	public String create(@PathVariable(name = "restaurantId") Integer restaurantId,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model,
			RedirectAttributes redirectAttributes) {
		if (userDetailsImpl == null) {
			// ユーザーが認証されていない場合、ログインページにリダイレクト
			return "redirect:/login";
		}

		Integer userId = userDetailsImpl.getUser().getId();
		FavoriteRegisterForm favoriteRegisterForm = new FavoriteRegisterForm();
		favoriteRegisterForm.setRestaurantId(restaurantId);
		favoriteRegisterForm.setUserId(userId);

		BindingResult bindingResult = new BeanPropertyBindingResult(favoriteRegisterForm, "favoriteRegisterForm");
		favoriteService.create(favoriteRegisterForm, bindingResult);

		if (bindingResult.hasErrors()) {
			// エラーがある場合、適切な処理を行う（例: エラーメッセージをモデルに追加して再表示）
			model.addAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/login"; // エラーが発生した場合、ログイン画面にリダイレクトする
		}

		// フラッシュメッセージを設定
		redirectAttributes.addFlashAttribute("message", "お気に入りに追加しました");
		// リダイレクト先のURLを生成してリダイレクト

		return "redirect:/restaurants/" + restaurantId;
	}

	@PostMapping("/{restaurantId}/delete")
	public String delete(@PathVariable(name = "restaurantId") Integer restaurantId,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, FavoriteRegisterForm favoriteRegisterForm,
			RedirectAttributes redirectAttributes, Model model) {

		if (userDetailsImpl == null) {
			return "redirect:/login";
		}

		Integer userId = userDetailsImpl.getUser().getId();
		favoriteRepository.deleteByRestaurantIdAndUserId(restaurantId, userId);

		// フラッシュメッセージを設定
		redirectAttributes.addFlashAttribute("message", "お気に入りを解除しました");
		return "redirect:/restaurants/" + restaurantId;
	}
}
