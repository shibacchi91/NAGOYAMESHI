package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteInputForm {
	@NotNull
	private Integer houseId;

	@NotNull
	private Integer userId;

}