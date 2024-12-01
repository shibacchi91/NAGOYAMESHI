package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.UserService;

@Controller
public class PasswordResetController {

	private final UserService userService;

	public PasswordResetController(UserService userService) {
		this.userService = userService;
	}

	// パスワードリセットフォームを表示
	@GetMapping("/auth/reset-password")
	public String showResetPasswordPage(@RequestParam("resetToken") String resetToken, Model model) {
		User user = userService.validateResetToken(resetToken);
		if (user == null) {
			model.addAttribute("errorMessage", "無効なトークンです。もう一度お試しください。");
			return "auth/forgot-password"; // トークンが無効な場合、リダイレクト先
		}
		model.addAttribute("resetToken", resetToken); // トークンをフォームに渡す
		return "auth/reset-password"; // reset-password.htmlを表示
	}

	// 新しいパスワードを更新
	@PostMapping("/auth/reset-password")
	public String resetPassword(@RequestParam("resetToken") String resetToken,
			//@RequestParam("password") String password,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			RedirectAttributes redirectAttributes, Model model) {
		User user = userService.validateResetToken(resetToken);
		if (user == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "無効なトークンです。もう一度お試しください。");
			return "redirect:/auth/forgot-password"; // トークンが無効な場合、リダイレクト先
		}

		// パスワードが一致するか確認
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("passwordMismatch", true);
			return "/auth/reset-password";
		}

		userService.updatePassword(user, newPassword); // パスワード更新処理
		redirectAttributes.addFlashAttribute("successMessage", "パスワードが正常にリセットされました。");
		return "redirect:/login"; // ログイン画面にリダイレクト
		//return "redirect:/login?passwordResetSuccess"
	}
}
