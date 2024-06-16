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
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/user")

public class UserController {
	private final UserRepository userRepository;
	private final UserService userService;
	private final UserDetailsServiceImpl userDetailsServiceImpl;

	 @Autowired
	public UserController(UserRepository userRepository, UserService userService, UserDetailsServiceImpl userDetailsServiceImpl) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
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

	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(),
				user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail(),
				user.getRole().getMembership());
		model.addAttribute("userEditForm", userEditForm);
		model.addAttribute("membershipOptions", Arrays.asList("無料会員", "有料会員"));
		return "user/edit";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes,@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		// 会員プランが「無料会員」の場合、membershipを「一般」に設定する
		if ("無料会員".equals(userEditForm.getMembership())) {
			userEditForm.setMembership("ROLE_GENERAL");
		}
		// 会員プランが「有料会員」の場合、membershipを「プレミアム」に設定する
		else if ("有料会員".equals(userEditForm.getMembership())) {
			userEditForm.setMembership("ROLE_PREMIUM");
		}
		// メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "user/edit";
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
}
