package com.sparta.msa_exam.order.entity.repository;

import com.sparta.msa_exam.order.entity.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM orders o JOIN FETCH o.orderProductList WHERE o.orderId = :orderId AND o.userId = :userId")
    Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId);
}
