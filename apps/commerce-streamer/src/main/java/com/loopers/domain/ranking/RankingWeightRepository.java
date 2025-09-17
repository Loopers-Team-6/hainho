package com.loopers.domain.ranking;

import java.util.Optional;

public interface RankingWeightRepository {
    RankingWeight save(RankingWeight rankingWeight);

    Optional<RankingWeight> findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(
            RankingTargetType type, WeightType weightType);
}
