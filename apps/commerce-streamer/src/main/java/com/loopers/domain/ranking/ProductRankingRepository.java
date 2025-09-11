package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.Map;

public interface ProductRankingRepository {
    void incrementScore(Map<Long, Double> productIdScoreMap, LocalDate actionDate);
}
