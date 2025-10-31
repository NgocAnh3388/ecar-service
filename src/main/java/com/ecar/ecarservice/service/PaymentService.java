package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.requests.PaymentRequest;
import com.ecar.ecarservice.payload.responses.PaymentHistoryResponse;
import com.ecar.ecarservice.payload.responses.PaymentResponse;
import com.paypal.api.payments.Payment;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

public interface PaymentService {
    PaymentResponse renew(PaymentRequest request, OidcUser oidcUser);

    Payment executePayment(
            String paymentId,
            String payerId
    );

    List<PaymentHistoryResponse> getPaymentHistory(OidcUser oidcUser);
}
