package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.entity.Order;
import java.io.Serializable;
import java.util.List;

public record OrderResponse (
        Long orderId,
        List<ProductResponse> productIds,
        Long userId
)  implements Serializable {
    public OrderResponse(Order order) {
        this(order.getOrderId(), order.getOrderProductList().stream().map(ProductResponse::new).toList(), order.getUserId());
    }
}
