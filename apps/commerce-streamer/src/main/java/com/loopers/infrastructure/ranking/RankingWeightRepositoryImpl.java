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
    private final RankingWeightRedisRepository rankingWeightRedisRepository;

    @Override
    public Optional<RankingWeight> findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(RankingTargetType type, WeightType weightType) {
        Optional<RankingWeight> optionalCachedRankingWeight = rankingWeightRedisRepository.findWeight(type, weightType);
        if (optionalCachedRankingWeight.isPresent()) {
            return optionalCachedRankingWeight;
        }
        Optional<RankingWeight> optionalRankingWeight = rankingWeightJpaRepository.findFirstByTypeAndWeightTypeOrderByCreatedAtDesc(type, weightType);
        optionalRankingWeight.ifPresent(rankingWeight -> rankingWeightRedisRepository.save(type, weightType, rankingWeight));
        return optionalRankingWeight;
    }

    @Override
    public RankingWeight save(RankingWeight rankingWeight) {
        return rankingWeightJpaRepository.save(rankingWeight);
    }
}
