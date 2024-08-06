package com.example.nagoyameshi.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	@Value("${stripe.api-key}")
	private String stripeApiKey;

	private final UserService userService;

	public StripeService(UserService userService) {
		this.userService = userService;
	}

	// セッションを作成し、Stripeに必要な情報を返す
	public String createStripeSession(Object form, HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
		String requestUrl = new String(httpServletRequest.getRequestURL());
        // successUrlとcancelUrlの設定
        String successUrl = "";
        String cancelUrl = "";
        
        if (form instanceof SignupForm) {
            successUrl = requestUrl.replaceAll("/signup/[A-Za-z]+", "") + "/subscription/authconfirm";
            cancelUrl = requestUrl.replace("/signup/[A-Za-z]+", "/signup");
        } else if (form instanceof UserEditForm) {
            successUrl = requestUrl.replaceAll("/user/[A-Za-z]+", "") + "/subscription/confirm";
            cancelUrl = requestUrl.replace("/user/[A-Za-z]+", "/user/edit");
        }
		SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setPriceData(
										SessionCreateParams.LineItem.PriceData.builder()
												.setProductData(
														SessionCreateParams.LineItem.PriceData.ProductData.builder()
																.setName(form instanceof SignupForm ? ((SignupForm) form).getName() : ((UserEditForm) form).getName())
																.build())
												.setUnitAmount((long) 300)
												.setCurrency("jpy")
												.build())
								.setQuantity(1L)
								.build())
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(
						requestUrl.replaceAll("/user/[A-Za-z]+", "") + "/user")
				.setCancelUrl(requestUrl.replace("/subscription/confirm", ""));

		SessionCreateParams.PaymentIntentData.Builder paymentIntentDataBuilder = SessionCreateParams.PaymentIntentData.builder();

		if (form instanceof SignupForm) {
			SignupForm signupForm = (SignupForm) form;
			paymentIntentDataBuilder
					.putMetadata("id", "0") // 新規作成のためIDは0
					.putMetadata("Name", signupForm.getName())
					.putMetadata("Furigana", signupForm.getFurigana())
					.putMetadata("PostalCode", signupForm.getPostalCode())
					.putMetadata("Address", signupForm.getAddress())
					.putMetadata("PhoneNumber", signupForm.getPhoneNumber())
					.putMetadata("Email", signupForm.getEmail())
					.putMetadata("Password", signupForm.getPassword())
					.putMetadata("MembershipType", signupForm.getMembershipType())
					.putMetadata("actionType", "create"); // 新規作成を示すフィールド
		} else if (form instanceof UserEditForm) {
			UserEditForm userEditForm = (UserEditForm) form;
			paymentIntentDataBuilder
					.putMetadata("id", userEditForm.getId().toString())
					.putMetadata("Name", userEditForm.getName())
					.putMetadata("Furigana", userEditForm.getFurigana())
					.putMetadata("PostalCode", userEditForm.getPostalCode())
					.putMetadata("Address", userEditForm.getAddress())
					.putMetadata("PhoneNumber", userEditForm.getPhoneNumber())
					.putMetadata("Email", userEditForm.getEmail())
					.putMetadata("actionType", "update"); // アップグレードを示すフィールド
		}

		paramsBuilder.setPaymentIntentData(paymentIntentDataBuilder.build());

		try {
			Session session = Session.create(paramsBuilder.build());
			System.out.println("Created session ID: " + session.getId());
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void processSessionCompleted(Event event) {
		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
		optionalStripeObject.ifPresentOrElse(stripeObject -> {
			Session session = (Session) stripeObject;
			SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

			try {
				session = Session.retrieve(session.getId(), params, null);
				Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
				String actionType = paymentIntentObject.get("actionType");
				System.out.println("Action Type: " + actionType);  // デバッグログ
				
				if ("create".equals(actionType)) {
					SignupForm signupForm = new SignupForm();
					signupForm.setName(paymentIntentObject.get("Name"));
					signupForm.setFurigana(paymentIntentObject.get("Furigana"));
					signupForm.setPostalCode(paymentIntentObject.get("PostalCode"));
					signupForm.setAddress(paymentIntentObject.get("Address"));
					signupForm.setPhoneNumber(paymentIntentObject.get("PhoneNumber"));
					signupForm.setEmail(paymentIntentObject.get("Email"));
					signupForm.setPassword(paymentIntentObject.get("Password"));
					signupForm.setMembershipType("ROLE_PREMIUM");
					userService.create(signupForm);
				} else if ("update".equals(actionType)) {
					UserEditForm userEditForm = new UserEditForm();
					userEditForm.setId(Integer.parseInt(paymentIntentObject.get("id")));
					userEditForm.setName(paymentIntentObject.get("Name"));
					userEditForm.setFurigana(paymentIntentObject.get("Furigana"));
					userEditForm.setPostalCode(paymentIntentObject.get("PostalCode"));
					userEditForm.setAddress(paymentIntentObject.get("Address"));
					userEditForm.setPhoneNumber(paymentIntentObject.get("PhoneNumber"));
					userEditForm.setEmail(paymentIntentObject.get("Email"));
					userEditForm.setMembership("ROLE_PREMIUM");
					userService.update(userEditForm);

				}

			} catch (StripeException e) {
				e.printStackTrace();
			}
			System.out.println("有料会員登録処理が成功しました。");
			System.out.println("Stripe API Version: " + event.getApiVersion());
			System.out.println("stripe-java Version: " + Stripe.VERSION);
		},
				() -> {
					System.out.println("有料会員登録処理が失敗しました。");
					System.out.println("Stripe API Version: " + event.getApiVersion());
					System.out.println("stripe-java Version: " + Stripe.VERSION);
				});
	}
}