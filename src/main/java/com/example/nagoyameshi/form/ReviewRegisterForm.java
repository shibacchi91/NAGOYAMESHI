package com.example.nagoyameshi.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterForm {
	private Integer restaurantId;

	private Integer userId;

	private String rating;

	private String review;


}
