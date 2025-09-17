package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingService {
    private final ProductRankingRepository productRankingRepository;

    public RankingInfo.Get getProductRanking(String date, Pageable pageable) {
        List<RankingInfo.Get.Item> items = productRankingRepository.getRankingWithPagination(date, pageable);
        return new RankingInfo.Get(items);
    }

    public Long getTotalCount(String date) {
        return productRankingRepository.getTotalCount(date);
    }

    public Optional<Long> getRanking(Long productId) {
        LocalDate curDate = ZonedDateTime.now().toLocalDate();
        return productRankingRepository.getRanking(curDate, productId);
    }
}
