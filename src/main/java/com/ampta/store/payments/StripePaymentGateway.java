package com.ampta.store.payments;

import com.ampta.store.orders.Order;
import com.ampta.store.orders.OrderItem;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@lombok.extern.slf4j.Slf4j
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("stripe-signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            log.info("Processing Stripe event: {}, type: {}", event.getId(), event.getType());

            return switch (event.getType()) {
                case "checkout.session.completed", "payment_intent.succeeded" -> {
                    var orderId = extractOrderId(event);
                    log.info("Payment succeeded for orderId: {}", orderId);
                    yield Optional.of(new PaymentResult(orderId, PaymentStatus.PAID));
                }

                case "payment_intent.payment_failed" -> {
                    var orderId = extractOrderId(event);
                    log.info("Payment failed for orderId: {}", orderId);
                    yield Optional.of(new PaymentResult(orderId, PaymentStatus.FAILED));
                }

                default -> {
                    log.info("Ignoring event type: {}", event.getType());
                    yield Optional.empty();
                }
            };
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature verification failed: {}", e.getMessage());
            throw new PaymentException("Invalid Signature");
        }
    }

    private Long extractOrderId(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentException("Could not deserialize stripe event. check the SDK and API version"));

        if (stripeObject instanceof Session session) {
            return Long.valueOf(session.getMetadata().get("order_id"));
        }

        if (stripeObject instanceof PaymentIntent paymentIntent) {
            return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
        }

        throw new PaymentException("Unsupported stripe object type: " + stripeObject.getClass().getName());

    }

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            var builder = SessionCreateParams
                    .builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                            .putMetadata("order_id", order.getId().toString()).build());

            order.getItems().forEach(item -> {
                var lineItem = createLineItem(item);

                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        } catch (StripeException e) {
            System.out.println(e.getMessage());
            throw new PaymentException();
        }
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }
}
