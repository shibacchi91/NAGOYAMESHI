package com.example.nagoyameshi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.nagoyameshi.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class StripeWebhookController {
    private final StripeService stripeService;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Stripe.apiKey = stripeApiKey;
        Event event = null;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if ("checkout.session.completed".equals(event.getType())) {
            stripeService.processSessionCompleted(event);
            
            // セッションにリダイレクト用のフラグを設定
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            attr.getRequest().getSession().setAttribute("subscriptionStatus", "completed");
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
    // GETリクエストをハンドリング
    @GetMapping("/stripe/checkout")
    public String handleBrowserBack(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String subscriptionStatus = (String) session.getAttribute("subscriptionStatus");

        if ("completed".equals(subscriptionStatus)) {
            // フラグが設定されている場合は成功画面にリダイレクト
            session.removeAttribute("subscriptionStatus"); // フラグをクリア
            return "redirect:/success";
        }

        // フラグがない場合はエラーページや適切なページにリダイレクト
        return "redirect:/error";
    }
}
