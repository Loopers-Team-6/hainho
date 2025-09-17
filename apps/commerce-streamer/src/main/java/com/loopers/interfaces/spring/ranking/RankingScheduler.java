package com.loopers.interfaces.spring.ranking;

import com.loopers.application.ranking.RankingFacade;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingScheduler {
    private final RankingFacade rankingFacade;

    @Scheduled(cron = "0 50 23 * * *", zone = "Asia/Seoul")
    public void carryOverDailyRanking() {
        rankingFacade.carryOverDailyRanking(ZonedDateTime.now().toLocalDate());
    }
}
