package com.ampta.store.payments;

import com.ampta.store.carts.CartEmptyException;
import com.ampta.store.carts.CartNotFoundException;
import com.ampta.store.common.ErrorDto;
import com.ampta.store.orders.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        log.info("Checkout request: {}", request);

        return checkoutService.checkout(request);

    }

    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload) {
        log.info("Received Stripe webhook. Headers: {}", headers);
        log.info("Payload: {}", payload);
        checkoutService.handleWebhookEvent(new com.ampta.store.payments.WebhookRequest(headers, payload));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }

    @ExceptionHandler({ CartNotFoundException.class, CartEmptyException.class })
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto(ex.getMessage()));
    }
}
