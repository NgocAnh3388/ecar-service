package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.SubscriptionInfo;
import com.ecar.ecarservice.payload.responses.SubscriptionInfoResponse;
import com.ecar.ecarservice.repositories.SubscriptionInfoRepository;
import com.ecar.ecarservice.service.SubscriptionService;
import com.ecar.ecarservice.service.UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionInfoRepository subscriptionInfoRepository;
    private final UserService userService;

    public SubscriptionServiceImpl(SubscriptionInfoRepository subscriptionInfoRepository,
                                   UserService userService) {
        this.subscriptionInfoRepository = subscriptionInfoRepository;
        this.userService = userService;
    }

    @Override
    public SubscriptionInfoResponse getSubscription(OidcUser user) {
        AppUser appUser = this.userService.getCurrentUser(user);
        return this.subscriptionInfoRepository.findFirstByOwnerId(appUser.getId())
                .map(this::fromSubscriptionInfo)
                .orElse(null);
    }

    private SubscriptionInfoResponse fromSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
        return new SubscriptionInfoResponse(
                subscriptionInfo.getId(),
                subscriptionInfo.getStartDate(),
                subscriptionInfo.getEndDate(),
                subscriptionInfo.getPaymentDate()
        );
    }
}
