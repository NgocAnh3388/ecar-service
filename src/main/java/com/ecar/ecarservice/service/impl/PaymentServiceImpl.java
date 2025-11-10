package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.PaymentHistory;
import com.ecar.ecarservice.entities.SubscriptionInfo;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.math.BigDecimal;
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
            AppUser appUser = userService.getCurrentUser(oidcUser);
            double AMOUNT_PER_YEAR = 1000.0;
            double totalAmountValue = AMOUNT_PER_YEAR * request.numOfYears();

            // === Tạo đối tượng thanh toán PayPal ===
            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format(Locale.US, "%.2f", totalAmountValue));

            Transaction transaction = new Transaction();
            transaction.setDescription("Payment for subscription renewal");
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

            Payment createdPayment = payment.create(apiContext);

            String redirectUrl = createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
                    .map(Links::getHref)
                    .findFirst()
                    .orElse(null);

            // === Lưu lịch sử thanh toán ===
            SubscriptionInfo subscriptionInfo = subscriptionInfoRepository
                    .findFirstByOwnerId(appUser.getId())
                    .orElseGet(() -> createNew(appUser.getId()));

            PaymentHistory paymentHistory = new PaymentHistory();
            paymentHistory.setNumOfYears(request.numOfYears());
            paymentHistory.setSubscriptionId(subscriptionInfo.getId());
            paymentHistory.setPaymentMethod("paypal");
            paymentHistory.setPaymentStatus(PaymentStatus.INIT.name());
            paymentHistory.setPaymentId(createdPayment.getId());
            paymentHistory.setAmount(BigDecimal.valueOf(totalAmountValue));

            paymentHistoryRepository.save(paymentHistory);

            return new PaymentResponse(redirectUrl);

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public Payment executePayment(String paymentId, String payerId) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            if (executedPayment.getState().equalsIgnoreCase(PaymentStatus.APPROVED.name())) {
                PaymentHistory history = paymentHistoryRepository.findFirstByPaymentId(executedPayment.getId());
                history.setPaymentStatus(PaymentStatus.APPROVED.name());
                paymentHistoryRepository.save(history);

                SubscriptionInfo info = subscriptionInfoRepository.findFirstById(history.getSubscriptionId());
                LocalDateTime now = LocalDateTime.now();
                info.setStartDate(now);
                info.setEndDate(now.plusYears(history.getNumOfYears()));
                info.setPaymentDate(now);
                subscriptionInfoRepository.save(info);
            }

            return executedPayment;

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PaymentHistoryResponse> getPaymentHistory(OidcUser oidcUser) {
        AppUser appUser = userService.getCurrentUser(oidcUser);
        Optional<SubscriptionInfo> info = subscriptionInfoRepository.findFirstByOwnerId(appUser.getId());

        return info.map(subscriptionInfo ->
                paymentHistoryRepository.findAllBySubscriptionId(subscriptionInfo.getId())
                        .stream()
                        .map(this::fromPaymentHistory)
                        .toList()
        ).orElse(List.of());
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
        return subscriptionInfoRepository.save(subscriptionInfo);
    }
}
