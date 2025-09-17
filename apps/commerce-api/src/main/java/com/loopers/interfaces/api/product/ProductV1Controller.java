package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductV1Controller implements ProductV1ApiSepc {

    private final ProductFacade productFacade;

    @Override
    @GetMapping("/products")
    public ApiResponse<ProductV1Dto.GetProducts.Response> getProducts(
            ProductV1Dto.GetProducts.Request request,
            Pageable pageable,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId
    ) {
        ProductResult.Get.Page page = productFacade.getProductPage(userId, request.brandId(), pageable);
        ProductV1Dto.GetProducts.Response response = ProductV1Dto.GetProducts.Response.from(page, pageable);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/products/{productId}")
    public ApiResponse<ProductV1Dto.GetDetail.Response> getProduct(
            @PathVariable Long productId,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId
    ) {
        ProductResult.Get.Detail detail = productFacade.getProductDetail(userId, productId);
        ProductV1Dto.GetDetail.Response response = ProductV1Dto.GetDetail.Response.from(detail);
        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/rankings")
    public ApiResponse<ProductV1Dto.GetProducts.Response> getProductRanking(
            ProductV1Dto.GetProductRankings.Request request,
            Pageable pageable,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId
    ) {
        ProductResult.Get.Page page = productFacade.getProductRankingPage(userId, request.date(), pageable);
        ProductV1Dto.GetProducts.Response response = ProductV1Dto.GetProducts.Response.from(page, pageable);
        return ApiResponse.success(response);
    }
}
