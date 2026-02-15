package com.ampta.store.payments;

import com.ampta.store.orders.Order;
import com.ampta.store.carts.CartEmptyException;
import com.ampta.store.carts.CartNotFoundException;
import com.ampta.store.carts.CartRepository;
import com.ampta.store.orders.OrderRepository;
import com.ampta.store.auth.AuthService;
import com.ampta.store.carts.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final com.ampta.store.payments.PaymentGateway paymentGateway;



    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request){
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        if(cart.isEmpty()){
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);

        try{
            var session = paymentGateway.createCheckoutSession(order);

            cartService.clearCart(cart.getId());

            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        }catch(PaymentException e){
            orderRepository.delete(order);
            throw e;
        }
    }

    public void handleWebhookEvent(com.ampta.store.payments.WebhookRequest request){
        paymentGateway
                .parseWebhookRequest(request)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });

    }
}
