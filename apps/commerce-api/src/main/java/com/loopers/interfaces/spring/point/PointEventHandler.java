package com.loopers.interfaces.spring.point;

import com.loopers.domain.payment.PointPaymentCreated;
import com.loopers.domain.point.PointService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PointEventHandler {
    private final PointService pointService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PointPaymentCreated event) {
        pointService.usePoint(event.userId(), event.amount(), event.orderId(), event.paymentId());
    }
}
