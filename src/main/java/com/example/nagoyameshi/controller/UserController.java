package com.example.nagoyameshi.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.security.UserDetailsServiceImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")

public class UserController {
	private final UserRepository userRepository;
	private final UserService userService;
	private final UserDetailsServiceImpl userDetailsServiceImpl;
	private final StripeService stripeService;

	@Autowired
	public UserController(UserRepository userRepository, UserService userService,
			UserDetailsServiceImpl userDetailsServiceImpl, StripeService stripeService) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.stripeService = stripeService;

	}

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		if (userDetailsImpl == null) {
			System.out.println("userDetailsImplはnullです。");
			// 何らかの処理を行う（例：エラーページにリダイレクトするなど）
			return "error";
		}

		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());

		model.addAttribute("user", user);
		model.addAttribute("membership", user.getRole().getMembership());
		return "user/index";
	}

	@GetMapping("/edit") // 新規会員登録
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(),
				user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail(),
				user.getRole().getMembership());
		model.addAttribute("userEditForm", userEditForm);
		model.addAttribute("membershipOptions", Arrays.asList("無料会員", "有料会員"));
		return "user/edit";
	}

	@PostMapping("/edit") // メンバーシップ選択時に処理開始
	public String processEdit(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			HttpServletRequest httpServletRequest, Model model) {

		// 会員プランが「無料会員」の場合、membershipを「ROLE_GENERAL」に設定する
		if ("無料会員".equals(userEditForm.getMembership())) {
			userEditForm.setMembership("ROLE_GENERAL");
		}
		// 会員プランが「有料会員」の場合、membershipを「ROLE_PREMIUM」に設定する
		else if ("有料会員".equals(userEditForm.getMembership())) {
			String sessionId = stripeService.createStripeSession(userEditForm, httpServletRequest);
			userEditForm.setMembership("ROLE_PREMIUM");
			model.addAttribute("userEditForm", userEditForm);
			model.addAttribute("sessionId", sessionId);

			return "subscription/confirm";
		}

		// 他のバリデーションと処理を行う

		userService.update(userEditForm);
		UserDetails updatedUserDetails = userDetailsServiceImpl.loadUserByUsername(userDetailsImpl.getUsername());

		// 新しい認証トークンを作成
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());

		// セキュリティコンテキストに新しい認証トークンを設定
		SecurityContextHolder.getContext().setAuthentication(authentication);

		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
		return "redirect:/user";
	}

	@PostMapping("/update") //会員情報更新
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			HttpServletRequest httpServletRequest, Model model) {

		// 会員プランが「無料会員」の場合、membershipを「ROLE_GENERAL」に設定する
		if ("ROLE_GENERAL".equals(userEditForm.getMembership())) {
			userEditForm.setMembership("ROLE_GENERAL");
		}
		// 会員プランが「有料会員」の場合、membershipを「ROLE_PREMIUM」に設定する
		else if ("ROLE_PREMIUM".equals(userEditForm.getMembership())) {
			String sessionId = stripeService.createStripeSession(userEditForm, httpServletRequest);
			userEditForm.setMembership("ROLE_PREMIUM");
			model.addAttribute("userEditForm", userEditForm);
			model.addAttribute("sessionId", sessionId);


			return "redirect:/subscription/confirm";

		}
		// メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "redirect:/user/edit";
		}

		userService.update(userEditForm);
		UserDetails updatedUserDetails = userDetailsServiceImpl.loadUserByUsername(userDetailsImpl.getUsername());

		// 新しい認証トークンを作成
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());

		// セキュリティコンテキストに新しい認証トークンを設定
		SecurityContextHolder.getContext().setAuthentication(authentication);

		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
		return "redirect:/user";
	}

	@GetMapping("/subscription/confirm")
	public String confirm(Model model) {
		return "subscription/confirm"; // subscription/confirm.html を表示
	}

	// 退会処理
	@PostMapping("/delete")
	public String deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			HttpServletRequest request, 
			HttpServletResponse response, 
			RedirectAttributes redirectAttributes) {
		if (userDetailsImpl == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "退会処理に失敗しました。");
			return "redirect:/user";
		}

		// ユーザーの削除処理
		userService.deleteUser(userDetailsImpl.getUser().getId(), request, response);


		redirectAttributes.addFlashAttribute("successMessage", "退会処理が完了しました。");
		return "redirect:/";
	}

}