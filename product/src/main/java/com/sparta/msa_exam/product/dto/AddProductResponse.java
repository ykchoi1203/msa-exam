package com.sparta.msa_exam.product.dto;

public record AddProductResponse(
        String name,
        Integer price,
        Integer quantity
) {
}
