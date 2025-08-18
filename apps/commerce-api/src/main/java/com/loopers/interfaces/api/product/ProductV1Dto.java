package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductV1Dto {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetProducts {
        public record Request(
                Long brandId
        ) {
        }

        public record Response(
                Page<Product> products
        ) {
            public static Response from(ProductResult.Get.Page page, Pageable pageable) {
                List<Product> productList = page.products().stream().map(Product::from).toList();
                PageImpl<Product> productPage = new PageImpl<>(productList, pageable, 10);
                return new Response(productPage);
            }

            public record Product(
                    Long id,
                    String name,
                    Long price,
                    Brand brand,
                    Like like
            ) {
                public static Product from(ProductResult.Get.Page.Product product) {
                    return new Product(
                            product.id(),
                            product.name(),
                            product.price(),
                            Brand.from(product.brand()),
                            Like.from(product.like())
                    );
                }
            }

            public record Brand(
                    Long id,
                    String name
            ) {
                public static Brand from(ProductResult.Get.Page.Brand brand) {
                    return new Brand(brand.id(), brand.name());
                }
            }

            public record Like(
                    Long count,
                    Boolean isLiked
            ) {
                public static Like from(ProductResult.Get.Page.Like like) {
                    return new Like(like.count(), like.isLiked());
                }
            }
        }
    }
}
