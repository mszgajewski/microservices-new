package com.mszgajewski.orderservice.controller;

import com.mszgajewski.orderservice.dto.OrderRequest;
import com.mszgajewski.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
   // @CircuitBreaker(name= "inventory", fallbackMathod = "fallBackMethod")
   // @TimeLimiter(name = "inventory")
   // @Retry(name = "inventory")

    public CompletableFuture <String> placeOrder(@RequestBody OrderRequest orderRequest){
        return CompletableFuture.supplyAsync(() ->orderService.placeOrder(orderRequest));
    }

    public CompletableFuture <String> fallbackMethod(@RequestBody OrderRequest orderRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "Coś poszło nie tak, prosimy spróbować później");
    }
}
