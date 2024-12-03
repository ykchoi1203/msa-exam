package com.sparta.msa_exam.product.dto;

import com.sparta.msa_exam.product.entity.Product;

public record ProductResponse (
        String name,
        Integer supportPrice,
        Integer quantity
){
    public ProductResponse(Product product) {
        this(product.getName(), product.getSupplyPrice(), product.getQuantity());
    }
}
