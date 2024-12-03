package com.sparta.msa_exam.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddProductRequest(
        String name,
        @JsonProperty("supply-price")
        Integer supplyPrice,
        Integer quantity
){
}
