package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProductRankingRepository {
    void incrementScore(Map<Long, Double> productIdScoreMap, LocalDate actionDate);

    List<RankingInfo.WithScore> getRankingAllWithScore(LocalDate date);

    void addRanking(LocalDate date, List<RankingInfo.WithScore> rankingInfos);
}
