package com.sparta.msa_exam.order.controller;

import com.sparta.msa_exam.order.dto.AddOrderRequest;
import com.sparta.msa_exam.order.dto.AddOrderResponse;
import com.sparta.msa_exam.order.dto.OrderResponse;
import com.sparta.msa_exam.order.dto.UpdateOrderRequest;
import com.sparta.msa_exam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    @Value("${server.port}")
    private String port;

    private final OrderService orderService;

    private final HttpHeaders headers = new HttpHeaders();

    @PostMapping
    public ResponseEntity<AddOrderResponse> createOrder(
            @RequestBody AddOrderRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(orderService.addOrders(request, userId));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "isAsc", defaultValue = "true") boolean isAsc) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(orderService.getOrders(userId, page, size, isAsc));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @RequestBody UpdateOrderRequest request,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(orderService.updateOrder(request, userId, orderId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(orderService.getOrder(userId, orderId));
    }

}
