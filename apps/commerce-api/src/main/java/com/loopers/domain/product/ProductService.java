package com.loopers.domain.product;

import com.loopers.domain.ranking.RankingInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductService {
    private final ProductRedisRepository productRedisRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private final ProductEventPublisher productEventPublisher;

    @Transactional
    public void deductStock(ProductStockCommand.Deduct command) {
        command.products()
                .stream()
                .forEach(this::deductStock);
    }

    private ProductStock deductStock(ProductStockCommand.Deduct.Product product) {
        ProductStock productStock = getProductStockWithLock(product.productId());
        productStock.deduct(product.quantityToDeduct());
        return productStock;
    }

    private ProductStock getProductStockWithLock(Long productId) {
        return productStockRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 재고 데이터가 존재하지 않습니다. productId: " + productId));
    }

    @Transactional(readOnly = true)
    public ProductInfo.Get getProductInfo(Long productId, Long userId) {
        Product product = productRedisRepository.find(productId)
                .orElse(getAndCacheProduct(productId));

        ProductFound event = ProductFound.of(productId, userId);
        productEventPublisher.publish(event);

        return ProductInfo.Get.from(product);
    }

    private Product getAndCacheProduct(Long productId) {
        Product product = productRepository.find(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "id에 해당하는 상품을 찾을 수 없습니다. productId: " + productId));
        productRedisRepository.set(productId, product);
        return product;
    }

    @Transactional(readOnly = true)
    public Page<ProductInfo.GetPage> getProductPage(Long userId, Long brandId, Pageable pageable) {
        return productRepository.getPage(userId, brandId, pageable);
    }

    @Transactional
    public void refundStock(ProductStockCommand.Refund command) {
        command.products()
                .stream()
                .forEach(this::refundStock);
    }

    private void refundStock(ProductStockCommand.Refund.Product product) {
        ProductStock productStock = getProductStockWithLock(product.productId());
        productStock.refund(product.quantityToRefund());
        productStockRepository.save(productStock);
    }

    @Transactional(readOnly = true)
    public List<ProductInfo.GetPage> getProductRankingPage(RankingInfo.Get rankingInfo, Long userId) {
        List<Long> productIds = rankingInfo.items().stream().map(RankingInfo.Get.Item::id).toList();
        List<ProductInfo.GetPage> products = productRepository.getPage(productIds, userId);
        return sortByRanking(productIds, products);
    }

    private List<ProductInfo.GetPage> sortByRanking(List<Long> sortedProductIds, List<ProductInfo.GetPage> products) {
        Map<Long, ProductInfo.GetPage> productMap = products.stream()
                .collect(Collectors.toMap(ProductInfo.GetPage::id, Function.identity()));

        return sortedProductIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
