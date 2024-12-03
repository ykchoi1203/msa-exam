package com.sparta.msa_exam.product.controller;

import com.sparta.msa_exam.product.dto.AddProductRequest;
import com.sparta.msa_exam.product.dto.AddProductResponse;
import com.sparta.msa_exam.product.dto.ProductResponse;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@RefreshScope
public class ProductController {

    @Value("${server.port}")
    private String port;

    private final ProductService productService;

    private final HttpHeaders headers = new HttpHeaders();


    @PostMapping
    public ResponseEntity<AddProductResponse> addProduct(@RequestBody AddProductRequest request,
                                                         @RequestHeader(value = "X-User-Id") Long userId) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(productService.addProduct(request, userId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "name") String sortType,
            @RequestParam(required = false, name = "keyword") String keyword,
            @RequestParam(required = false, name = "min") Integer min,
            @RequestParam(required = false, name = "max") Integer max,
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            @RequestParam(defaultValue = "true", name = "isAsc") boolean isAsc
            ) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(productService.getProducts(keyword, min, max, page, size, sortType, isAsc));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        headers.set("server-port", port);
        return ResponseEntity.ok().headers(headers).body(productService.getProductById(id));
    }

    @GetMapping("/{id}/reduceQuantity")
    public void reduceProductQuantity(@PathVariable Long id, @RequestParam("quantity") Integer quantity) {
        productService.reduceProductQuantity(id, quantity);
    }

}
