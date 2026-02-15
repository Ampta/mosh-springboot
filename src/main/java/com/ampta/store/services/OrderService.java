package com.ampta.store.services;

import com.ampta.store.dtos.OrderDto;
import com.ampta.store.exceptions.OrderNotFoundException;
import com.ampta.store.mappers.OrderMapper;
import com.ampta.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {

        var user = authService.getCurrentUser();
        var orders = orderRepository.getOrdersByCustomer(user);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId) {
        var order = orderRepository
                .getOrderWithItems(orderId)
                .orElseThrow(OrderNotFoundException::new);

        var user = authService.getCurrentUser();
        if(!order.isPlacedBy(user)) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        return orderMapper.toDto(order);
    }
}
