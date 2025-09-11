package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

@Tag(name = "Product V1 API", description = "상품 관련 API입니다.")
public interface ProductV1ApiSepc {
    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회합니다."
    )
    ApiResponse<ProductV1Dto.GetProducts.Response> getProducts(
            @Schema(
                    name = "상품 조회 요청",
                    description = "상품 목록 조회에 필요한 정보를 담고 있는 요청 객체"
            )
            ProductV1Dto.GetProducts.Request request,
            @Schema(
                    name = "페이지 정보",
                    description = "상품 목록을 조회할 때 사용할 페이지 정보"
            )
            Pageable pageable,
            @Schema(
                    name = "X-USER-ID",
                    description = "조회할 사용자의 ID"
            )
            Long userId
    );

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 상세 정보를 조회합니다."
    )
    ApiResponse<ProductV1Dto.GetDetail.Response> getProduct(
            @Schema(
                    name = "productId",
                    description = "조회할 상품의 ID"
            )
            Long productId,
            @Schema(
                    name = "X-USER-ID",
                    description = "조회할 사용자의 ID"
            )
            Long userId
    );

    @Operation(
            summary = "상품 랭킹 조회",
            description = "상품 랭킹 정보를 조회합니다."
    )
    ApiResponse<ProductV1Dto.GetProducts.Response> getProductRanking(
            @Schema(
                    name = "상품 랭킹 조회 요청",
                    description = "상품 랭킹 조회에 필요한 정보를 담고 있는 요청 객체"
            )
            ProductV1Dto.GetProductRankings.Request request,
            @Schema(
                    name = "페이지 정보",
                    description = "상품 목록을 조회할 때 사용할 페이지 정보"
            )
            Pageable pageable,
            @Schema(
                    name = "X-USER-ID",
                    description = "조회할 사용자의 ID"
            )
            Long userId
    );
}
