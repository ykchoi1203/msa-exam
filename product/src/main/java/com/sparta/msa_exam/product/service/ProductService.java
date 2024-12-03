package com.sparta.msa_exam.product.service;

import com.sparta.msa_exam.product.dto.AddProductRequest;
import com.sparta.msa_exam.product.dto.AddProductResponse;
import com.sparta.msa_exam.product.dto.ProductResponse;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.entity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public AddProductResponse addProduct(AddProductRequest request, Long userId) {
        Product product = Product.builder()
                .name(request.name())
                .supplyPrice(request.supplyPrice())
                .quantity(request.quantity())
                .userId(userId)
                .build();

        product = productRepository.save(product);

        return new AddProductResponse(product.getName(), product.getSupplyPrice(), product.getQuantity());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String keyword, Integer min, Integer max, Integer page, Integer size, String sortType, boolean isAsc) {
        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findAll(keyword, min, max, sortType, pageable, isAsc).map(ProductResponse::new);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or has been deleted"));
        return new ProductResponse(product);
    }

    @Transactional
    public void reduceProductQuantity(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough quantity for product ID: " + productId);
        }

        product.reduceQuantity(quantity);
        productRepository.save(product);
    }
}
