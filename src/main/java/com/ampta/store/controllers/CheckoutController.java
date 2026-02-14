package com.ampta.store.controllers;

import com.ampta.store.dtos.CheckoutRequest;
import com.ampta.store.dtos.CheckoutResponse;
import com.ampta.store.exceptions.CartEmptyException;
import com.ampta.store.exceptions.CartNotFoundException;
import com.ampta.store.exceptions.ErrorDto;
import com.ampta.store.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(
            @Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }


    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex){
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new  ErrorDto(ex.getMessage())
        );
    }
}

