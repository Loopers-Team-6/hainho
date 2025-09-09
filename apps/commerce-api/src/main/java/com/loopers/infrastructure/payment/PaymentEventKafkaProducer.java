package com.loopers.infrastructure.payment;

import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.domain.payment.CardPaymentCreated;
import com.loopers.domain.payment.PaymentFailed;
import com.loopers.domain.payment.PaymentSucceed;
import com.loopers.domain.payment.PointPaymentCreated;
import com.loopers.infrastructure.kafka.KafkaMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEventKafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(PaymentSucceed event) {
        KafkaMessage<PaymentSucceed> kafkaMessage = KafkaMessage.from(event);
        // 감사 로그 용
        // 결제 성공 이벤트는 결제 ID를 키로 사용하여 순서 보장
        kafkaTemplate.send(KafkaTopics.ORDER, event.paymentId().toString(), kafkaMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(PaymentFailed event) {
        KafkaMessage<PaymentFailed> kafkaMessage = KafkaMessage.from(event);
        // 감사 로그 용
        // 결제 실패 이벤트는 결제 ID를 키로 사용하여 순서 보장
        kafkaTemplate.send(KafkaTopics.ORDER, event.orderId().toString(), kafkaMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(CardPaymentCreated event) {
        KafkaMessage<CardPaymentCreated> kafkaMessage = KafkaMessage.from(event);
        // 감사 로그 용
        // 카드 결제 생성 이벤트는 주문 ID를 키로 사용하여 순서 보장
        kafkaTemplate.send(KafkaTopics.ORDER, event.orderId().toString(), kafkaMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(PointPaymentCreated event) {
        KafkaMessage<PointPaymentCreated> kafkaMessage = KafkaMessage.from(event);
        // 감사 로그 용
        // 포인트 결제 생성 이벤트는 주문 ID를 키로 사용하여 순서 보장
        kafkaTemplate.send(KafkaTopics.ORDER, event.orderId().toString(), kafkaMessage);
    }
}
