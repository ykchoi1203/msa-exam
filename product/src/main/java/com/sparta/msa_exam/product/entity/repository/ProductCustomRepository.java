package com.sparta.msa_exam.product.entity.repository;

import com.sparta.msa_exam.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {
    Page<Product> findAll(String keyword, Integer min, Integer max, String sortType, Pageable pageable, boolean isAsc);
}
