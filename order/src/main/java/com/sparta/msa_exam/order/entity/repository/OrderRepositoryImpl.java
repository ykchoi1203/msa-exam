package com.sparta.msa_exam.order.entity.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.QOrder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import static com.sparta.msa_exam.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findAll(Long userId, Pageable pageable, boolean isAsc) {
        List<Order> results = queryFactory
                .selectFrom(order)
                .where(userId != null ? order.userId.eq(userId) : null)
                .orderBy(orderSpecifier(isAsc))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(order.count())
                .from(order)
                .where(userId != null ? order.userId.eq(userId) : null);

        return new PageImpl<>(results, pageable, total.fetchOne() == null ? 0 : total.fetchOne());
    }

    private OrderSpecifier<?> orderSpecifier(Boolean isAsc) {
        return isAsc ? order.createdAt.asc() : order.createdAt.desc();
    }

}
