package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingTargetType;
import com.loopers.domain.ranking.RankingWeight;
import com.loopers.domain.ranking.RankingWeightRepository;
import com.loopers.domain.ranking.WeightType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingWeightRepositoryImpl implements RankingWeightRepository {
    private final RankingWeightJpaRepository rankingWeightJpaRepository;

    @Override
    public Optional<RankingWeight> findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(RankingTargetType type, WeightType weightType) {
        return rankingWeightJpaRepository.findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(type, weightType);
    }

    @Override
    public RankingWeight save(RankingWeight rankingWeight) {
        return rankingWeightJpaRepository.save(rankingWeight);
    }
}
