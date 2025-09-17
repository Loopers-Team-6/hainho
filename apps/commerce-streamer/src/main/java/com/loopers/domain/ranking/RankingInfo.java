package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingInfo {
    public record WithScore(
            Long productId,
            Double score
    ) {
    }
}
