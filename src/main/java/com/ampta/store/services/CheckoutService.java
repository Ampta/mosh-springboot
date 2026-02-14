package com.ampta.store.services;

import com.ampta.store.dtos.CheckoutRequest;
import com.ampta.store.dtos.CheckoutResponse;
import com.ampta.store.entities.Order;
import com.ampta.store.exceptions.CartEmptyException;
import com.ampta.store.exceptions.CartNotFoundException;
import com.ampta.store.exceptions.ErrorDto;
import com.ampta.store.repositories.CartRepository;
import com.ampta.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;


    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        if(cart.isEmpty()){
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);
        cartService.clearCart(cart.getId());

        return new CheckoutResponse(order.getId());
    }
}
