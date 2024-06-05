package com.example.nagoyameshi.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ReservationRegisterForm {
    private Integer restaurantId;
    
    private Integer userId;    
        
    private String checkinDate;    
            
    private String checkinTime;
    
    private Integer numberOfPeople;
    
    private Integer amount;
}
