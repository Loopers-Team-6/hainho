package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRankingService {
    private static final double DEFAULT_VIEW_WEIGHT = 0.05;
    private static final double DEFAULT_PURCHASE_WEIGHT = 1.0;
    private static final double DEFAULT_LIKE_WEIGHT = 0.25;

    private final RankingWeightRepository rankingWeightRepository;
    private final ProductRankingRepository productRankingRepository;

    public void rankByViews(Long productId, Long views, LocalDate rankDate) {
        double score = calculateScoreByView(views);
        productRankingRepository.incrementScore(productId, score, rankDate);
    }

    private double calculateScoreByView(Long views) {
        double weight = getWeight(WeightType.VIEW);
        return views * weight;
    }

    public void rankByPurchases(Long productId, Long price, Long amount, LocalDate rankDate) {
        double score = calculateScoreByPriceAndAmount(price, amount);
        productRankingRepository.incrementScore(productId, score, rankDate);
    }

    private double calculateScoreByPriceAndAmount(Long price, Long amount) {
        double weight = getWeight(WeightType.PURCHASE);
        return (price * amount) * weight;
    }

    public void rankByLikes(Long productId, Long likes, LocalDate rankDate) {
        double score = calculateScoreByLike(likes);
        productRankingRepository.incrementScore(productId, score, rankDate);
    }

    private double calculateScoreByLike(Long likes) {
        double weight = getWeight(WeightType.LIKE);
        return likes * weight;
    }

    private double getWeight(WeightType weightType) {
        Optional<RankingWeight> optionalWeight = rankingWeightRepository.findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(RankingTargetType.PRODUCT, weightType);
        if (optionalWeight.isEmpty()) {
            return switch (weightType) {
                case VIEW -> DEFAULT_VIEW_WEIGHT;
                case PURCHASE -> DEFAULT_PURCHASE_WEIGHT;
                case LIKE -> DEFAULT_LIKE_WEIGHT;
            };
        }
        return optionalWeight.get().getWeight();
    }
}
