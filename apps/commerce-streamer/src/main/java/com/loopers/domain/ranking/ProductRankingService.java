package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRankingService {
    private static final double DEFAULT_VIEW_WEIGHT = 0.05;
    private static final double DEFAULT_PURCHASE_WEIGHT = 1.0;
    private static final double DEFAULT_LIKE_WEIGHT = 0.25;
    private static final double SCORE_CAP = 0.05;

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

    public List<RankingInfo.WithScore> getRankingAll(LocalDate date) {
        return productRankingRepository.getRankingAllWithScore(date);
    }

    public void addRanking(LocalDate date, List<RankingInfo.WithScore> rankingInfos) {
        productRankingRepository.addRanking(date, rankingInfos);
    }

    public List<RankingInfo.WithScore> normalizeScore(List<RankingInfo.WithScore> rankingInfos) {
        if (rankingInfos.isEmpty()) {
            return List.of();
        }
        double factor = calculateFactor(rankingInfos);
        return applyFactor(rankingInfos, factor);
    }

    private List<RankingInfo.WithScore> applyFactor(List<RankingInfo.WithScore> rankingInfos, double factor) {
        return rankingInfos.stream()
                .map(info -> new RankingInfo.WithScore(
                        info.productId(),
                        info.score() * factor
                ))
                .toList();
    }

    private double calculateFactor(List<RankingInfo.WithScore> rankingInfos) {
        double maxScore = rankingInfos.getFirst().score();
        return SCORE_CAP / maxScore;
    }
}
