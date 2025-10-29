package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.responses.SubscriptionInfoResponse;
import com.ecar.ecarservice.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<SubscriptionInfoResponse> getSubscription(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(this.subscriptionService.getSubscription(oidcUser));
    }

}