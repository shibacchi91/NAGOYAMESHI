package com.example.nagoyameshi.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service

public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final FavoriteRepository favoriteRepository;
	private final ReservationRepository reservationRepository;
	private final ReviewRepository reviewRepository;
	private final EmailService emailService;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			FavoriteRepository favoriteRepository, ReservationRepository reservationRepository,
			ReviewRepository reviewRepository, EmailService emailService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.favoriteRepository = favoriteRepository;
		this.reservationRepository = reservationRepository;
		this.reviewRepository = reviewRepository;
		this.emailService = emailService;

	}

	/*新規登録*/
	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();

		//signupForm の membershipType に基づいて Role を選択する
		Role role;
		if ("ROLE_PREMIUM".equals(signupForm.getMembershipType())) {
			role = roleRepository.findByMembership("ROLE_PREMIUM");//有料会員
		} else {
			role = roleRepository.findByMembership("ROLE_GENERAL");//無料会員
		}

		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);

		return userRepository.save(user);
	}

	/*	編集*/
	@Transactional
	public void update(UserEditForm userEditForm) {
		Integer userId = userEditForm.getId();
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Userが見つかりません"));

		user.setName(userEditForm.getName());
		user.setFurigana(userEditForm.getFurigana());
		user.setPostalCode(userEditForm.getPostalCode());
		user.setAddress(userEditForm.getAddress());
		user.setPhoneNumber(userEditForm.getPhoneNumber());
		user.setEmail(userEditForm.getEmail());

		Role role;

		switch (userEditForm.getMembership()) {
		case "ROLE_GENERAL":
			role = roleRepository.findById(1).orElseThrow(() -> new RuntimeException("Roleが見つかりません"));
			break;
		case "ROLE_PREMIUM":
			role = roleRepository.findById(2).orElseThrow(() -> new RuntimeException("Roleが見つかりません"));
			break;
		case "未登録":
			// 未登録の場合は、Roleエンティティは存在しないため、nullを返す
			role = null;
			break;
		default:
			throw new IllegalArgumentException("不正な会員プラン: " + userEditForm.getMembership());
		}

		user.setRole(role);

		userRepository.save(user);
	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	// ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	// メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

	@Transactional
	public void deleteUser(Integer userId, HttpServletRequest request, HttpServletResponse response) {

		// favorites テーブルから関連するレコードを削除
		favoriteRepository.deleteByUserId(userId);

		// reservations テーブルから関連するレコードを削除
		reservationRepository.deleteByUserId(userId);

		// reviews テーブルから関連するレコードを削除
		reviewRepository.deleteByUserId(userId);

		userRepository.deleteById(userId);

		// 現在の認証情報を取得
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			// セキュリティコンテキストからログアウトを実行
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
	}

	/* パスワードリセットトークンの作成とメール送信 */
	@Transactional
	public boolean sendPasswordResetEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return false;
		}

		// トークンの生成
		String resetToken = UUID.randomUUID().toString();
		user.setResetToken(resetToken);
		user.setTokenExpiration(LocalDateTime.now().plusHours(1)); // 1時間の有効期限
		userRepository.save(user);

		// パスワードリセット用URLの作成
		String resetUrl = "http://localhost:8080/auth/reset-password?resetToken=" + resetToken;

		// メール送信（簡易化）
		// 実際には、EmailServiceクラスでメール送信を行うことを推奨
		emailService.sendEmail(
				user.getEmail(),
				"パスワードリセット",
				"以下のリンクからパスワードをリセットしてください: " + resetUrl);

		return true;
	}

	/* トークンの検証 */
	@Transactional(readOnly = true)
	public User validateResetToken(String resetToken) {
		User user = userRepository.findByResetToken(resetToken);
		if (user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
			return null; // 無効または期限切れ
		}
		return user;
	}

	/* パスワードの更新 */
	@Transactional
	public void updatePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetToken(null); // トークンを無効化
		user.setTokenExpiration(null);
		userRepository.save(user);
	}

	/* メール送信（簡易的な例） 
	private void sendEmail(String to, String subject, String body) {
		// JavaMailSenderを使ったメール送信ロジックをここに記述
	}*/

}
