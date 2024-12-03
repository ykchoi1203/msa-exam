package com.sparta.msa_exam.order.dto;


import com.sparta.msa_exam.order.entity.OrderProduct;
import java.io.Serializable;

public record ProductResponse(
        Long productId,
        Integer quantity
) implements Serializable {
    public ProductResponse(OrderProduct product) {
        this(product.getProductId(), product.getQuantity());
    }
}
