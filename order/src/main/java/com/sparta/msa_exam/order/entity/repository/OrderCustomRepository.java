package com.sparta.msa_exam.order.entity.repository;

import com.sparta.msa_exam.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
    Page<Order> findAll(Long userId, Pageable pageable, boolean isAsc);
}
