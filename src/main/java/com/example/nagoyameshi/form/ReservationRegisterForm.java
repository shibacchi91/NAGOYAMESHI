package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ReservationRegisterForm {
	
	
	@NotNull
    private Integer restaurantId;
	@NotNull
    private Integer userId;    
	@NotNull
    private String checkinDate;    
	@NotNull    
    private String checkinTime;
	@NotNull
    private Integer numberOfPeople;
	@NotNull
    private Integer amount;
}
