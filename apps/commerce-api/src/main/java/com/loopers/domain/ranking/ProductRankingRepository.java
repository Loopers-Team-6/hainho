package com.loopers.domain.ranking;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductRankingRepository {
    List<RankingInfo.Get.Item> getRankingWithPagination(String date, Pageable pageable);

    Long getTotalCount(String date);

    Optional<Long> getRanking(LocalDate date, Long productId);
}
