package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.dto.ProductResponse;
import com.sparta.msa_exam.order.dto.AddOrderRequest;
import com.sparta.msa_exam.order.dto.AddOrderResponse;
import com.sparta.msa_exam.order.dto.OrderResponse;
import com.sparta.msa_exam.order.dto.ProductRequest;
import com.sparta.msa_exam.order.dto.UpdateOrderRequest;
import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.OrderProduct;
import com.sparta.msa_exam.order.entity.repository.OrderCustomRepository;
import com.sparta.msa_exam.order.entity.repository.OrderProductRepository;
import com.sparta.msa_exam.order.entity.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderCustomRepository orderCustomRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductClient productClient;


    @Transactional
    public AddOrderResponse addOrders(AddOrderRequest request, long userId) {
        Order order = Order.builder().userId(userId).orderProductList(new ArrayList<>()).build();

        for (ProductRequest product : request.products()) {
            ProductResponse productResponse = getProductWithFallback(product.productId());
            if (productResponse == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + product.productId() + " not found");
            } else if (productResponse.quantity() < product.quantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + product.productId() + " quantity is less than " + product.quantity());
            }

        }

        // 수량 차감
        for(ProductRequest product : request.products()) {
            productClient.reduceProductQuantity(product.productId(), product.quantity());

            // OrderProduct 생성 및 추가
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .productId(product.productId())
                    .quantity(product.quantity())
                    .build();
            order.getOrderProductList().add(orderProduct);
        }

        order.setOrderProductList(orderProductRepository.saveAll(order.getOrderProductList()));
        order = orderRepository.save(order);

        return new AddOrderResponse(order.getOrderProductList().stream().map(
                com.sparta.msa_exam.order.dto.ProductResponse::new).toList());
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    private ProductResponse getProductWithFallback(Long productId) {
        return productClient.getProduct(productId).getBody();
    }

    // Fallback 로직: 실패 시 null 반환
    private ProductResponse getProductFallback(Long productId, Throwable throwable) {
        return null; // 실패 시 null 반환하여 사용자에게 Fallback 메시지 전달
    }

    @Transactional(readOnly = true)
    // cachenames : 적용할 캐시 규칙을 지정하기 위한 이름, key : 캐시 데이터를 구분하기 위해 활용하는 값 (인자들 중 key로 사용할 인자 설정)
    @Cacheable(cacheNames = "orderAllCache", key = "args[0]")
    public Page<OrderResponse> getOrders(Long userId, int page, int size, boolean isAsc) {
        Pageable pageable = PageRequest.of(page, size);

        return orderCustomRepository.findAll(userId, pageable, isAsc).map(OrderResponse::new);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "orderCache", key = "args[2]"), // orderId 기반 캐시 제거
            @CacheEvict(cacheNames = "orderAllCache", key = "args[1]") // userId 기반 캐시 제거
    })
    public OrderResponse updateOrder(UpdateOrderRequest request, Long userId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if(!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to update this order");
        }

        OrderProduct orderProduct = OrderProduct.builder().productId(request.productId()).order(order).quantity(
                request.quantity()).build();
        orderProductRepository.save(orderProduct);

        order.getOrderProductList().add(OrderProduct.builder().productId(request.productId()).order(order).quantity(1).build());

        return new OrderResponse(order);
    }

    @Transactional(readOnly = true)
    // cachenames : 적용할 캐시 규칙을 지정하기 위한 이름, key : 캐시 데이터를 구분하기 위해 활용하는 값 (인자들 중 key로 사용할 인자 설정)
    @Cacheable(cacheNames = "orderCache", key = "args[1]")
    public OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        );

        return new OrderResponse(order);
    }
}
