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
	public String createStripeSession(UserEditForm userEditForm, HttpServletRequest httpServletRequest) {
		Stripe.apiKey = "sk_test_51P53pqDtHZHUakkXNzKwgyNpZ1Z9eCxrpt4wPUH8HvpSP7knGTe9qwrKAbf7VVa2QyHjMVnlv34mmFi25RMEbYWJ00W1C5yIMH";
		String requestUrl = new String(httpServletRequest.getRequestURL());
		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setPriceData(
										SessionCreateParams.LineItem.PriceData.builder()
												.setProductData(
														SessionCreateParams.LineItem.PriceData.ProductData.builder()
																.setName(userEditForm.getName())
																.build())
												.setUnitAmount((long) 300)
												.setCurrency("jpy")
												.build())
								.setQuantity(1L)
								.build())
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(
						requestUrl.replaceAll("subscription/confirm", "") + "/subscription/confirm")
				.setCancelUrl(requestUrl.replace("/subscription/confirm", ""))
				.setPaymentIntentData(
						SessionCreateParams.PaymentIntentData.builder()
								.putMetadata("id", userEditForm.getId().toString())
								.putMetadata("Name", userEditForm.getName())
								.putMetadata("Furigana", userEditForm.getFurigana())
								.putMetadata("PostalCode", userEditForm.getPostalCode())
								.putMetadata("Address", userEditForm.getAddress())
								.putMetadata("PhoneNumber", userEditForm.getPhoneNumber())
								.putMetadata("Email", userEditForm.getEmail())

								.build())
				.build();
		try {
			Session session = Session.create(params);
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}

	
    public void processSessionCompleted(Event event) {
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
        optionalStripeObject.ifPresentOrElse(stripeObject -> {
            Session session = (Session)stripeObject;
            SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

            try {
                session = Session.retrieve(session.getId(), params, null);
                Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();

                SignupForm signupForm = new SignupForm();
                signupForm.setName(paymentIntentObject.get("Name"));
                signupForm.setFurigana(paymentIntentObject.get("Furigana"));
                signupForm.setPostalCode(paymentIntentObject.get("PostalCode"));
                signupForm.setAddress(paymentIntentObject.get("Address"));
                signupForm.setPhoneNumber(paymentIntentObject.get("PhoneNumber"));
                signupForm.setEmail(paymentIntentObject.get("Email"));
                signupForm.setPassword("defaultPassword"); // パスワードは適切な方法で設定する必要があります
                signupForm.setMembershipType("ROLE_PREMIUM");
                signupForm.setStripePaymentId(paymentIntentObject.get("stripePaymentId"));

                userService.create(signupForm);
                
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