package com.loopers.domain.ranking;

import java.time.LocalDate;

public interface ProductRankingRepository {
    void incrementScore(Long productId, double score, LocalDate actionDate);
}
