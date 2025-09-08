package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class LikeService {
    private final LikeProductRepository likeProductRepository;
    private final LikeProductCountRepository likeProductCountRepository;
    private final LikeEventPublisher likeEventPublisher;

    @Transactional
    public void likeProduct(Long userId, Long productId) {
        if (likeProductRepository.exists(userId, productId)) {
            return;
        }
        LikeProduct likeProduct = LikeProduct.create(userId, productId);
        likeProductRepository.save(likeProduct);

        LikeProductCreated event = LikeProductCreated.of(userId, productId);
        likeEventPublisher.publish(event);
    }

    @Transactional
    public void likeProductCancel(Long userId, Long productId) {
        Optional<LikeProduct> optionalLikeProduct = likeProductRepository.find(userId, productId);
        if (optionalLikeProduct.isEmpty()) {
            return;
        }
        LikeProduct likeProduct = optionalLikeProduct.get();
        likeProductRepository.delete(likeProduct);

        LikeProductDeleted event = LikeProductDeleted.of(userId, productId);
        likeEventPublisher.publish(event);
    }

    @Transactional
    public void increaseLikeProductCount(Long productId) {
        likeProductCountRepository.increaseLikeCount(productId);
    }

    @Transactional
    public void decreaseLikeProductCount(Long productId) {
        likeProductCountRepository.decreaseLikeCount(productId);
    }

    @Transactional(readOnly = true)
    public LikeInfo.Get getLikeProductInfo(Long userId, Long productId) {
        LikeProductCount likeProductCount = likeProductCountRepository.find(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품 좋아요 카운트 데이터가 존재하지 않습니다. productId: " + productId));
        Boolean isLiked = isExistsLikeProduct(userId, productId);
        return LikeInfo.Get.from(likeProductCount, isLiked);
    }

    private boolean isExistsLikeProduct(Long userId, Long productId) {
        if (userId == null) {
            return false;
        }
        return likeProductRepository.exists(userId, productId);
    }
}
