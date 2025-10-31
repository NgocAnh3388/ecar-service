package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.requests.PaymentExecuteRequest;
import com.ecar.ecarservice.payload.requests.PaymentRequest;
import com.ecar.ecarservice.payload.responses.PaymentHistoryResponse;
import com.ecar.ecarservice.payload.responses.PaymentResponse;
import com.ecar.ecarservice.payload.responses.SubscriptionInfoResponse;
import com.ecar.ecarservice.service.PaymentService;
import com.ecar.ecarservice.service.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    public SubscriptionController(SubscriptionService subscriptionService,
                                  PaymentService paymentService) {
        this.subscriptionService = subscriptionService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<SubscriptionInfoResponse> getSubscription(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(this.subscriptionService.getSubscription(oidcUser));
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST)
    public ResponseEntity<PaymentResponse> renewSubscription(@AuthenticationPrincipal OidcUser oidcUser,
                                                             @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(this.paymentService.renew(paymentRequest, oidcUser));
    }

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public ResponseEntity<Void> execute(@AuthenticationPrincipal OidcUser user,
                                        @RequestBody PaymentExecuteRequest request) {
        this.paymentService.executePayment(request.paymentId(), request.payerId());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/payment-history", method = RequestMethod.GET)
    public ResponseEntity<List<PaymentHistoryResponse>> getPaymentHistory(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(this.paymentService.getPaymentHistory(oidcUser));
    }

}
