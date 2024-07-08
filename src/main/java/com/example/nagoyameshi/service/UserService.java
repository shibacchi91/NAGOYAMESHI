package com.example.nagoyameshi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service

public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/*新規登録*/
	@Transactional
	public User  create(SignupForm signupForm){
		User user = new User();

		//signupForm の membershipType に基づいて Role を選択する
		Role role;
		if ("ROLE_PREMIUM".equals(signupForm.getMembershipType()))  {
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

}
