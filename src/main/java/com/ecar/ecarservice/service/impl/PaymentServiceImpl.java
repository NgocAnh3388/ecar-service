package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.PaymentHistory;
import com.ecar.ecarservice.enitiies.SubscriptionInfo;
import com.ecar.ecarservice.enums.PaymentStatus;
import com.ecar.ecarservice.payload.requests.PaymentRequest;
import com.ecar.ecarservice.payload.responses.PaymentHistoryResponse;
import com.ecar.ecarservice.payload.responses.PaymentResponse;
import com.ecar.ecarservice.repositories.PaymentHistoryRepository;
import com.ecar.ecarservice.repositories.SubscriptionInfoRepository;
import com.ecar.ecarservice.service.PaymentService;
import com.ecar.ecarservice.service.UserService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final APIContext apiContext;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final SubscriptionInfoRepository subscriptionInfoRepository;
    private final UserService userService;

    @Value("${paypal.successUrl}")
    private String successUrl;

    @Value("${paypal.cancelUrl}")
    private String cancelUrl;

    public PaymentServiceImpl(APIContext apiContext,
                              PaymentHistoryRepository paymentHistoryRepository,
                              SubscriptionInfoRepository subscriptionInfoRepository,
                              UserService userService) {
        this.apiContext = apiContext;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.subscriptionInfoRepository = subscriptionInfoRepository;
        this.userService = userService;
    }

    @Override
    public PaymentResponse renew(PaymentRequest request, OidcUser oidcUser) {
        try {
            AppUser appUser = this.userService.getCurrentUser(oidcUser);
            Amount amount = new Amount();
            amount.setCurrency("USD");
            double AMOUNT_PER_YEAR = 1000.0;
            amount.setTotal(String.format(Locale.forLanguageTag("USD"), "%.2f", AMOUNT_PER_YEAR  * request.numOfYears()));

            Transaction transaction = new Transaction();
            transaction.setDescription("payment for renew");
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(cancelUrl);
            redirectUrls.setReturnUrl(successUrl);
            payment.setRedirectUrls(redirectUrls);

            Payment rs = payment.create(apiContext);

            String redirectUrl = rs.getLinks().stream()
                    .filter(l -> l.getRel().equalsIgnoreCase("approval_url"))
                    .findFirst()
                    .map(Links::getHref)
                    .orElse(null);

            PaymentHistory paymentHistory = new PaymentHistory();
            SubscriptionInfo subscriptionInfo = this.subscriptionInfoRepository
                    .findFirstByOwnerId(appUser.getId())
                    .orElseGet(() -> this.createNew(appUser.getId()));
            paymentHistory.setNumOfYears(request.numOfYears());
            paymentHistory.setSubscriptionId(subscriptionInfo.getId());
            paymentHistory.setPaymentMethod("paypal");
            paymentHistory.setPaymentStatus(PaymentStatus.INIT.name());
            paymentHistory.setPaymentId(rs.getId());
            this.paymentHistoryRepository.save(paymentHistory);

            return new PaymentResponse(redirectUrl);

        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    @Transactional
    public Payment executePayment(
            String paymentId,
            String payerId
    ) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            Payment rs = payment.execute(apiContext, paymentExecution);
            if (rs.getState().equalsIgnoreCase(PaymentStatus.APPROVED.name())) {
                PaymentHistory history = this.paymentHistoryRepository.findFirstByPaymentId(rs.getId());
                history.setPaymentStatus(PaymentStatus.APPROVED.name());
                this.paymentHistoryRepository.save(history);

                SubscriptionInfo info = this.subscriptionInfoRepository.findFirstById(history.getSubscriptionId());
                info.setStartDate(LocalDateTime.now());
                info.setEndDate(LocalDateTime.now().plusYears(history.getNumOfYears()));
                info.setPaymentDate(LocalDateTime.now());
                this.subscriptionInfoRepository.save(info);
            }
            return rs;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<PaymentHistoryResponse> getPaymentHistory(OidcUser oidcUser) {
        AppUser appUser = this.userService.getCurrentUser(oidcUser);
        Optional<SubscriptionInfo> info = this.subscriptionInfoRepository.findFirstByOwnerId(appUser.getId());
        return info.map(subscriptionInfo -> this.paymentHistoryRepository.findAllBySubscriptionId(subscriptionInfo.getId())
                .stream()
                .map(this::fromPaymentHistory)
                .toList()).orElse(null);
    }

    private PaymentHistoryResponse fromPaymentHistory(PaymentHistory history) {
        return new PaymentHistoryResponse(
                history.getCreatedAt(),
                history.getPaymentMethod(),
                history.getPaymentStatus(),
                history.getNumOfYears()
        );
    }

    private SubscriptionInfo createNew(Long ownerId) {
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
        subscriptionInfo.setOwnerId(ownerId);
        return this.subscriptionInfoRepository.save(subscriptionInfo);
    }
}
