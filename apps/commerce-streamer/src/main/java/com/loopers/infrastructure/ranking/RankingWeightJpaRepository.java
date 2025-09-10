package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingTargetType;
import com.loopers.domain.ranking.RankingWeight;
import com.loopers.domain.ranking.WeightType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingWeightJpaRepository extends JpaRepository<RankingWeight, Long> {
    RankingWeight save(RankingWeight rankingWeight);

    Optional<RankingWeight> findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(
            RankingTargetType type, WeightType weightType);
}
