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
    private static final double DEFAULT_VIEW_WEIGHT = 0.1;
    private static final double DEFAULT_PURCHASE_WEIGHT = 0.01;
    private static final double DEFAULT_LIKE_WEIGHT = 1.0;
    private static final double SCORE_CAP = 0.05;

    // 기준가격(원)
    // 카테고리별 중앙가/대표가로 잡으면 좋음.
    private static final long REF_PRICE = 50_000L;

    // Isoelastic 지수 α (0<α≤1): α=1이면 선형(압축 없음), α가 작아질수록 고가 압축·저가 완화가 강해짐.
    // 운영 권장 범위: 0.75~0.90 (도메인 데이터로 튜닝)
    private static final double PRICE_ALPHA = 0.80;

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
        double transformedPrice = transformPriceIsoelastic(price);
        return (transformedPrice * amount) * weight;
    }

    /**
     * Isoelastic(거듭제곱) 가격 변환으로 가격 영향력을 완만하게 만듭니다.
     * <p>
     * 정의: p' = REF_PRICE * (max(price,0) / REF_PRICE) ^ PRICE_ALPHA
     * <p>
     * 성질:
     * - p = REF_PRICE일 때 p' = REF_PRICE (기준점 보정)
     * - 0 < PRICE_ALPHA < 1 이면 오목(고가 압축·저가 완화), α=1이면 항등(변환 없음)
     * - 상수 탄력성: d ln p' / d ln p = α (탄력성이 일정)
     * <p>
     * 주의:
     * - REF_PRICE > 0 이어야 합니다(0이면 분모 0).
     *
     * @param price 원가격(원, 음수는 0으로 처리)
     * @return 변환된 유효가격(스코어 산식에 사용)
     */
    private double transformPriceIsoelastic(long price) {
        long p = Math.max(0L, price);
        return REF_PRICE * Math.pow(p / (double) REF_PRICE, PRICE_ALPHA);
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
