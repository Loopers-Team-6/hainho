package com.loopers.infrastructure.product;

import com.loopers.domain.product.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.loopers.domain.brand.QBrand.brand;
import static com.loopers.domain.like.QLikeProduct.likeProduct;
import static com.loopers.domain.like.QLikeProductCount.likeProductCount;
import static com.loopers.domain.product.QProduct.product;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final ProductPageTotalCountRedisTemplate productPageTotalCountRedisTemplate;

    @Override
    public Optional<Product> find(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Page<ProductInfo.GetPage> getPage(Long userId, Long brandId, Pageable pageable) {
        Expression<Boolean> isLikedExpression =
                (userId != null) ? likeProduct.id.isNotNull()
                        : Expressions.constant(false);

        JPAQuery<ProductInfo.GetPage> query = queryFactory.select(
                        new QProductInfo_GetPage(
                                product.id,
                                product.name.value,
                                product.price.value,
                                new QProductInfo_GetPage_Brand(
                                        brand.id,
                                        brand.name.value
                                ),
                                new QProductInfo_GetPage_Like(
                                        likeProductCount.countValue.value,
                                        isLikedExpression
                                )
                        ))
                .from(product)
                .leftJoin(brand).on(brand.id.eq(product.brandId))
                .leftJoin(likeProductCount).on(likeProductCount.productId.eq(product.id));

        if (brandId != null) {
            query.where(product.brandId.eq(brandId));
        }

        if (userId != null) {
            query.leftJoin(likeProduct)
                    .on(likeProduct.productId.eq(product.id)
                            .and(likeProduct.userId.eq(userId)));
        }

        switch (pageable.getSort().toList().stream()
                .map(Sort.Order::getProperty)
                .findFirst()
                .orElse("default")) {
            case "latest" -> query.orderBy(product.createdAt.desc());
            case "price_asc" -> query.orderBy(product.price.value.asc());
            case "like_count_desc" -> query.orderBy(likeProductCount.countValue.value.desc());
            default -> query.orderBy(product.id.asc());
        }

        List<ProductInfo.GetPage> fetched = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = productPageTotalCountRedisTemplate.find(brandId)
                .orElseGet(() -> getAndCachePageTotalCount(brandId));

        return new PageImpl<>(fetched, pageable, count);
    }

    private Long getAndCachePageTotalCount(Long brandId) {
        JPAQuery<Long> countQuery = queryFactory.select(product.count())
                .from(product);
        if (brandId != null) {
            countQuery.where(product.brandId.eq(brandId));
        }
        Long totalCount = countQuery.fetchCount();
        productPageTotalCountRedisTemplate.set(brandId, totalCount);
        return totalCount;
    }

    @Override
    public List<ProductInfo.GetPage> getPage(List<Long> productIds, Long userId) {
        Expression<Boolean> isLikedExpression =
                (userId != null) ? likeProduct.id.isNotNull()
                        : Expressions.constant(false);

        JPAQuery<ProductInfo.GetPage> query = queryFactory.select(
                        new QProductInfo_GetPage(
                                product.id,
                                product.name.value,
                                product.price.value,
                                new QProductInfo_GetPage_Brand(
                                        brand.id,
                                        brand.name.value
                                ),
                                new QProductInfo_GetPage_Like(
                                        likeProductCount.countValue.value,
                                        isLikedExpression
                                )
                        ))
                .from(product)
                .leftJoin(brand).on(brand.id.eq(product.brandId))
                .leftJoin(likeProductCount).on(likeProductCount.productId.eq(product.id))
                .where(product.id.in(productIds));

        if (userId != null) {
            query.leftJoin(likeProduct)
                    .on(likeProduct.productId.eq(product.id)
                            .and(likeProduct.userId.eq(userId)));
        }

        return query.fetch();
    }
}