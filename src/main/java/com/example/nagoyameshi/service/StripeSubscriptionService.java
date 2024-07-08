package com.example.nagoyameshi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;

public class StripeSubscriptionService {

	static {
		// Set your secret key. Remember to switch to your live secret key in production.
		// See your keys here: https://dashboard.stripe.com/apikeys
		Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
	}

	public Customer createCustomer(String email, String token) throws StripeException {
		Map<String, Object> customerParams = new HashMap<>();
		customerParams.put("email", email);
		customerParams.put("source", token);

		return Customer.create(customerParams);
	}

	public Subscription createSubscription(String customerId, String priceId) throws StripeException {
		Map<String, Object> item = new HashMap<>();
		item.put("price", priceId);

		List<Map<String, Object>> items = new ArrayList<>();
		items.add(item);

		Map<String, Object> params = new HashMap<>();
		params.put("customer", customerId);
		params.put("items", items);

		return Subscription.create(params);
	}

	public void cancelSubscription(String subscriptionId) throws StripeException {
		Subscription subscription = Subscription.retrieve(subscriptionId);
		subscription.cancel();
	}
}
