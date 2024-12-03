package com.sparta.msa_exam.order.dto;

import java.util.List;

public record AddOrderResponse(
        List<ProductResponse> products
) {
}
