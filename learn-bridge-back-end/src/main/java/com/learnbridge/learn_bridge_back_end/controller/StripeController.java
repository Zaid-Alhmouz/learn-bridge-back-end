package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.SetupIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = "http://localhost:4200")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/api/stripe/setup-intent")
    public Map<String, String> createSetupIntent(
            @AuthenticationPrincipal SecurityUser loggedUser
    ) throws StripeException {
        String customerId = loggedUser.getUser().getStripeCustomerId();
        SetupIntent intent = stripeService.createSetupIntent(customerId);
        return Map.of("clientSecret", intent.getClientSecret());
    }
}
