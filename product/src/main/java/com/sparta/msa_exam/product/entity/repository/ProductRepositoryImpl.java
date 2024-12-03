package com.sparta.msa_exam.product.entity.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.product.entity.Product;
import com.sparta.msa_exam.product.entity.QProduct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import static com.sparta.msa_exam.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findAll(String keyword, Integer min, Integer max, String sortType, Pageable pageable,
                                 boolean isAsc) {
        List<Product> results = queryFactory
                .selectFrom(product)
                .where(keyword != null && !keyword.isEmpty() ? product.name.containsIgnoreCase(keyword) : null,
                        priceBetween(min, max))
                .orderBy(orderSpecifier(sortType, isAsc))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(product.count())
                .from(product)
                .where(keyword != null && !keyword.isEmpty() ?product.name.containsIgnoreCase(keyword) : null,
                        priceBetween(min, max));

        return new PageImpl<>(results, pageable, total.fetchOne() == null ? 0 : total.fetchOne());
    }

    private OrderSpecifier<?> orderSpecifier(String sortType, Boolean isAsc) {
        return sortType.equals("name") ? isAsc ? QProduct.product.name.asc() : QProduct.product.name.desc() : isAsc ? QProduct.product.supplyPrice.asc() : QProduct.product.supplyPrice.desc();
    }

    private BooleanExpression priceBetween(Integer min, Integer max) {
        if(min != null && max != null) {
            return product.supplyPrice.between(min, max);
        } else if(min != null) {
            return product.supplyPrice.goe(min);
        } else if(max != null) {
            return product.supplyPrice.loe(max);
        }

        return null;
    }


}
