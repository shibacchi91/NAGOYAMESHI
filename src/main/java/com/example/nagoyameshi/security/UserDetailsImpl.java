package com.example.nagoyameshi.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;


@Component
	public class UserDetailsImpl implements UserDetails {
	     private  User user;
	     private final Collection<GrantedAuthority> authorities;
	     
	
	     
	     
	     public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
	         this.user = user;
	         this.authorities = authorities;
				/* System.out.println("UserDetailsImplインスタンスが作成されました。");*/
	     }
	     
	     public User getUser() {
	         return user;
	     }
	     
	     // ユーザー情報を更新するためのメソッド
	     public void setUser(User user) {
	         this.user = user;
	     }
	     
	     // ハッシュ化済みのパスワードを返す
	     @Override
	     public String getPassword() {
	         return user.getPassword();
	     }
	     
	     // ログイン時に利用するユーザー名（メールアドレス）を返す
	     @Override
	     public String getUsername() {
	         return user.getEmail();
	     }
	     
	     // ロールのコレクションを返す
	     @Override
	     public Collection<? extends GrantedAuthority> getAuthorities() {
	         return authorities;
	     }
	     
	     // アカウントが期限切れでなければtrueを返す
	     @Override
	     public boolean isAccountNonExpired() {
	         return true;
	     }
	     
	     // ユーザーがロックされていなければtrueを返す
	     @Override
	     public boolean isAccountNonLocked() {
	         return true;
	     }    
	     
	     // ユーザーのパスワードが期限切れでなければtrueを返す
	     @Override
	     public boolean isCredentialsNonExpired() {
	         return true;
	     }
	     
	     // ユーザーが有効であればtrueを返す
	     @Override
	     public boolean isEnabled() {
	         return user.getEnabled();
	     }
}
