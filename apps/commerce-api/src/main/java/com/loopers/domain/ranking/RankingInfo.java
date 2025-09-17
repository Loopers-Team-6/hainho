package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingInfo {
    public record Get(
            List<Item> items
    ) {
        public record Item(
                Long id
        ) {
        }
    }
}
