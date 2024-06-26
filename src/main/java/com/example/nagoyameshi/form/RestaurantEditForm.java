package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class RestaurantEditForm {

	@NotNull
	private Integer id;

	@NotBlank(message = "店舗名を入力してください。")
	private String name;

	// カテゴリが空であっても問題無し
	private String category;

	private MultipartFile imageFile;

	@NotBlank(message = "説明を入力してください。")
	private String description;

	@NotNull(message = "利用料金を入力してください。")
	@Min(value = 1, message = "利用料金は1円以上に設定してください。")
	private Integer price;

	@NotNull(message = "定員を入力してください。")
	@Min(value = 1, message = "定員は1人以上に設定してください。")
	private Integer capacity;

	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;

	@NotBlank(message = "住所を入力してください。")
	private String address;

	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;

	// 手動でコンストラクターを定義する例
	public RestaurantEditForm(Integer id, String name, String category, MultipartFile imageFile,
			String description, Integer price, Integer capacity, String postalCode,
			String address, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.imageFile = imageFile;
		this.description = description;
		this.price = price;
		this.capacity = capacity;
		this.postalCode = postalCode;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}
}
