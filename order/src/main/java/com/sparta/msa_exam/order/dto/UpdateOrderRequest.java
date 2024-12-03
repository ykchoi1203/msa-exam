package com.sparta.msa_exam.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateOrderRequest(
        @JsonProperty("product-id")
        Long productId,
        Integer quantity
) {
}
