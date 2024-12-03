package com.sparta.msa_exam.order.client.dto;

public record ProductResponse (
        String name,
        Integer supportPrice,
        Integer quantity
){
}

