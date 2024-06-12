	package com.example.nagoyameshi.controller;
	
	import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReservationService;
	
	@Controller
	
	public class ReservationController {
	
		private final ReservationRepository reservationRepository;
		private final RestaurantRepository restaurantRepository;
		private final ReservationService reservationService;
	
		public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
				ReservationService reservationService) {
			this.reservationRepository = reservationRepository;
			this.restaurantRepository = restaurantRepository;
			this.reservationService = reservationService;
	
		}
	
		@GetMapping("/reservations")
		public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
				@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
				Model model) {
			User user = userDetailsImpl.getUser();
			Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
	
			model.addAttribute("reservationPage", reservationPage);
	
			return "reservations/index";
		}
	
		@GetMapping("/restaurants/{id}/reservations/input")
		public String input(@PathVariable(name = "id") Integer id,
		                    @ModelAttribute @Validated ReservationInputForm reservationInputForm,
		                    BindingResult bindingResult,
		                    RedirectAttributes redirectAttributes,
		                    Model model) {
		    Restaurant restaurant = restaurantRepository.getReferenceById(id);
		    Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
		    Integer capacity = restaurant.getCapacity();

		    if (numberOfPeople != null) {
		        if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
		            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople",
		                    "利用人数が定員を超えています。");
		            bindingResult.addError(fieldError);
		        }
		    }

		    if (bindingResult.hasErrors()) {
		        model.addAttribute("restaurant", restaurant);
		        model.addAttribute("reservationInputForm", reservationInputForm); // ここを追加
		        model.addAttribute("errorMessage", "予約内容に不備があります。");
		        return "restaurants/show"; // show.html に遷移
		    }

		    redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);

		    return "redirect:/restaurants/{id}/reservations/confirm";
		}
	
		@GetMapping("/restaurants/{id}/reservations/confirm")
		public String confirm(@PathVariable(name = "id") Integer id,
				@ModelAttribute ReservationInputForm reservationInputForm,
				@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
				Model model) {
			Restaurant restaurant = restaurantRepository.getReferenceById(id);
			User user = userDetailsImpl.getUser();
	
			//来店日と来店時間を取得する
			LocalDate checkinDate = reservationInputForm.getCheckinDate();
			LocalTime checkinTime = reservationInputForm.getCheckinTime();
			// 利用料金を計算する
			Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
			Integer price = restaurant.getPrice();
			Integer amount = reservationService.calculateAmount(numberOfPeople, price);
	
			ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(restaurant.getId(), user.getId(),
					checkinDate.toString(),  checkinTime.toString(),reservationInputForm.getNumberOfPeople(), amount);
	
			model.addAttribute("restaurant", restaurant);
			model.addAttribute("reservationRegisterForm", reservationRegisterForm);
	
			return "reservations/confirm";
		}
	
		/*		@PostMapping("/restaurants/{id}/reservations/create")
				public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {
					reservationService.create(reservationRegisterForm);
			
					return "redirect:/reservations?reserved";
				}*/
	
		@GetMapping("/restaurants/{id}/reservations")
		public String listReservationsForRestaurant(@PathVariable("id") Integer restaurantId, Model model) {
			List<Reservation> reservations = reservationRepository.findByRestaurantId(restaurantId);
	
			model.addAttribute("reservations", reservations);
			model.addAttribute("restaurantId", restaurantId);
	
			return "reservations/index";
		}
	
		@PostMapping("/reservations/{id}/cancel")
		public String cancelReservation(@PathVariable("id") Integer reservationId) {
			reservationService.cancelReservation(reservationId);
			return "redirect:/reservations";
		}
	}
