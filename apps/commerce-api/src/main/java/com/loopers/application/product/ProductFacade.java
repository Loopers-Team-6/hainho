package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductFacade {
    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;

    public ProductResult.Get.Detail getProductDetail(Long userId, Long productId) {
        ProductInfo.Get productInfo = productService.getProductInfo(productId, userId);
        BrandInfo.Get brandInfo = brandService.getBrandInfo(productInfo.brandId());
        LikeInfo.Get likeInfo = likeService.getLikeProductInfo(userId, productId);

        return ProductResult.Get.Detail.from(productInfo, brandInfo, likeInfo);
    }

    public ProductResult.Get.Page getProductPage(Long userId, Long brandId, Pageable pageable) {
        return ProductResult.Get.Page.from(productService.getProductPage(userId, brandId, pageable));
    }
}
