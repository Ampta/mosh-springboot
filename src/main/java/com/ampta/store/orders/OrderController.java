package com.ampta.store.orders;

import com.ampta.store.common.ErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getOrders() {
         return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable("orderId") Long orderId) {
        return orderService.getOrder(orderId);
    }


    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleOrderNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorDto(e.getMessage())
        );
    }
}
