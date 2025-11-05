package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.responses.SubscriptionInfoResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface SubscriptionService {
    SubscriptionInfoResponse getSubscription(OidcUser user);
}
