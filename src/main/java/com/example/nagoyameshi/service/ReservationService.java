package com.example.nagoyameshi.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service

public class ReservationService {
    private final ReservationRepository reservationRepository;  
    private final RestaurantRepository restaurantRepository;  
    private final UserRepository userRepository;  
    
    
    public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;  
        this.restaurantRepository = restaurantRepository;  
        this.userRepository = userRepository;  
    }    
    
    @Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) { 
        Reservation reservation = new Reservation();
        Restaurant restaurant = restaurantRepository.getReferenceById(reservationRegisterForm.getRestaurantId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        LocalDate checkinDate = LocalDate.parse(reservationRegisterForm.getCheckinDate());
        LocalTime checkinTime = LocalTime.parse(reservationRegisterForm.getCheckinTime());
                
        reservation.setRestaurant(restaurant);
        reservation.setUser(user);
        reservation.setCheckinDate(checkinDate);
        reservation.setCheckinTime(checkinTime);
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
        reservation.setAmount(reservationRegisterForm.getAmount());


        reservationRepository.save(reservation);
    } 
    // 利用人数が定員以下かどうかをチェックする
    public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
        return numberOfPeople <= capacity;
    }
    
    // 利用料金を計算する
    public Integer calculateAmount(Integer numberOfPeople, Integer price) {
    	return numberOfPeople * price;
    }    
	
    public void cancelReservation(Integer reservationId) {
        // 予約IDを使用して予約を取得します
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("指定された予約が見つかりませんでした。"));

        // 予約を削除します
        reservationRepository.delete(reservation);
    }
}
