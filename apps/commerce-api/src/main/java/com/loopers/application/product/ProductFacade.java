package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingInfo;
import com.loopers.domain.ranking.RankingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductFacade {
    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;
    private final RankingService rankingService;

    public ProductResult.Get.Detail getProductDetail(Long userId, Long productId) {
        ProductInfo.Get productInfo = productService.getProductInfo(productId, userId);
        BrandInfo.Get brandInfo = brandService.getBrandInfo(productInfo.brandId());
        LikeInfo.Get likeInfo = likeService.getLikeProductInfo(userId, productId);
        Long ranking = rankingService.getRanking(productId).orElse(null);
        return ProductResult.Get.Detail.from(productInfo, brandInfo, likeInfo, ranking);
    }

    public ProductResult.Get.Page getProductPage(Long userId, Long brandId, Pageable pageable) {
        return ProductResult.Get.Page.from(productService.getProductPage(userId, brandId, pageable));
    }

    public ProductResult.Get.Page getProductRankingPage(Long userId, String date, Pageable pageable) {
        RankingInfo.Get productRankingInfo = rankingService.getProductRanking(date, pageable);
        Long totalCount = rankingService.getTotalCount(date);
        List<ProductInfo.GetPage> productInfos = productService.getProductRankingPage(productRankingInfo, userId);
        return ProductResult.Get.Page.from(productInfos, pageable, totalCount);
    }
}
