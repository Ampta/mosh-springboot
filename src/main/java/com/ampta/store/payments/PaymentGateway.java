package com.ampta.store.payments;

import com.ampta.store.entities.Order;
import com.ampta.store.payments.CheckoutSession;
import com.ampta.store.payments.PaymentResult;
import com.ampta.store.payments.WebhookRequest;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
}
