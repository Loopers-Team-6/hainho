package com.loopers.domain.ranking;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRankingRepository {
    List<RankingInfo.Get.Item> getRankingWithPagination(String key, Pageable pageable);

    Long getTotalCount(String date);
}
