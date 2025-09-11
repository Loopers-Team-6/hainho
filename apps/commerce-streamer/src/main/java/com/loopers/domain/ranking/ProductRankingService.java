package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRankingService {
    private static final double DEFAULT_VIEW_WEIGHT = 0.05;
    private static final double DEFAULT_PURCHASE_WEIGHT = 1.0;
    private static final double DEFAULT_LIKE_WEIGHT = 0.25;

    private final RankingWeightRepository rankingWeightRepository;
    private final ProductRankingRepository productRankingRepository;

    @Transactional(readOnly = true)
    public double calculateScoreByView(Long views) {
        double weight = getWeight(WeightType.VIEW);
        return views * weight;
    }

    @Transactional(readOnly = true)
    public double calculateScoreByPriceAndAmount(Long price, Long amount) {
        double weight = getWeight(WeightType.PURCHASE);
        return (price * amount) * weight;
    }

    @Transactional(readOnly = true)
    public double calculateScoreByLikeCreated(Long count) {
        double weight = getWeight(WeightType.LIKE);
        return count * weight;
    }

    @Transactional(readOnly = true)
    public double calculateScoreByLikeDeleted(Long count) {
        double weight = getWeight(WeightType.LIKE);
        return count * weight * -1;
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

    public void rank(Map<Long, Double> productIdScoreMap, LocalDate producedDate) {
        productRankingRepository.incrementScore(productIdScoreMap, producedDate);
    }
}
