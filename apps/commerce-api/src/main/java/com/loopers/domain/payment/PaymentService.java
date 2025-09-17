package com.loopers.domain.payment;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public PaymentInfo.Get createCardPayment(PaymentCommand.Create command) {
        Payment payment = Payment.create(command);
        paymentRepository.save(payment);

        CardPaymentCreated event = CardPaymentCreated.from(payment, command.cardType(), command.cardNumber());
        paymentEventPublisher.publish(event);

        return PaymentInfo.Get.from(payment);
    }

    @Transactional
    public PaymentInfo.Get createPointPayment(PaymentCommand.Create command) {
        Payment payment = Payment.create(command);
        Payment savedPayment = paymentRepository.save(payment);

        PointPaymentCreated event = PointPaymentCreated.from(savedPayment, command.userId());
        paymentEventPublisher.publish(event);

        return PaymentInfo.Get.from(payment);
    }

    @Transactional
    public PaymentInfo.Get markResult(PaymentCommand.MarkResult command) {
        Payment payment = getPaymentWithLock(command.paymentId());
        markRequestStatus(payment, command);
        return PaymentInfo.Get.from(payment);
    }

    private Payment getPaymentWithLock(Long paymentId) {
        return paymentRepository.findByIdWithLock(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제를 찾을 수 없습니다. ID: " + paymentId));
    }

    private void markRequestStatus(Payment payment, PaymentCommand.MarkResult command) {
        if (command.resultStatus().equals("SUCCESS")) {
            payment.markRequested(command.transactionKey());
        } else if (command.resultStatus().equals("READ_TIMEOUT")) {
            payment.markRequested();
        } else if (command.resultStatus().equals("FAILED")) {
            payment.markFailed();
            PaymentFailed event = PaymentFailed.of(payment.getOrderId(), payment.getId());
            paymentEventPublisher.publish(event);
        } else {
            throw new IllegalArgumentException("결제 결과 상태가 유효하지 않습니다: " + command.resultStatus());
        }
    }

    @Transactional
    public PaymentInfo.Get markFinalResult(PaymentCommand.MarkFinalResult command) {
        Payment payment = getPaymentWithLock(command.transactionKey());
        markFinalStatus(payment, command);
        return PaymentInfo.Get.from(payment);
    }

    @Transactional
    public void completePointPayment(Long orderId, Long paymentId) {
        Payment payment = getPaymentWithLock(paymentId);
        payment.completed();
    }

    private Payment getPaymentWithLock(String transactionKey) {
        return paymentRepository.findByTransactionKeyWithLock(transactionKey)
                .orElseThrow(() -> new EntityNotFoundException("결제를 찾을 수 없습니다. TransactionKey: " + transactionKey));
    }

    private void markFinalStatus(Payment payment, PaymentCommand.MarkFinalResult command) {
        if (command.resultStatus().equals("SUCCESS")) {
            payment.markCompleted(command.transactionKey());
            PaymentSucceed event = PaymentSucceed.from(payment);
            paymentEventPublisher.publish(event);
        } else if (command.resultStatus().equals("FAILED")) {
            payment.markFailed();
        } else {
            throw new IllegalArgumentException("결제 결과 상태가 유효하지 않습니다: " + command.resultStatus());
        }
    }

    public void verifyPaymentResult(String orderId, String transactionKey, String status) {
        PaymentQuery.Card.Payment command = new PaymentQuery.Card.Payment(transactionKey);
        PaymentInfo.Card.Get paymentInfo = paymentGateway.findCardPaymentResult(command);
        validatePaymentInfo(orderId, status, paymentInfo);
        PgPaymentCompleted event = PgPaymentCompleted.of(orderId, transactionKey, status);
        paymentEventPublisher.publish(event);
    }

    private void validatePaymentInfo(String orderId, String status, PaymentInfo.Card.Get paymentInfo) {
        if (!paymentInfo.status().equals(status)) {
            throw new IllegalStateException("결제 상태가 일치하지 않습니다. 요청된 상태: " + status + ", 실제 상태: " + paymentInfo.status());
        }
        if (!paymentInfo.orderId().equals(orderId)) {
            throw new IllegalStateException("결제 주문 ID가 일치하지 않습니다. 요청된 주문 ID: " + orderId + ", 실제 주문 ID: " + paymentInfo.orderId());
        }
    }

    public PaymentInfo.Card.Result requestCardPayment(PaymentCommand.Card.Payment command) {
        PaymentInfo.Card.Result result = paymentGateway.requestCardPayment(command);
        PgPaymentRequested event = PgPaymentRequested.of(command.orderId(), result.transactionKey(), result.status());
        paymentEventPublisher.publish(event);
        return result;
    }

    public PaymentInfo.Card.Get getCardPayment(PaymentQuery.Card.Payment query) {
        return paymentGateway.findCardPaymentResult(query);
    }

    public PaymentInfo.Card.Order getCardOrder(PaymentQuery.Card.Order query) {
        return paymentGateway.findCardOrderResult(query);
    }

    public void verifyOrderPaymentResult(Long orderId) {
        PaymentQuery.Card.Order query = new PaymentQuery.Card.Order(orderId);
        PaymentInfo.Card.Order cardOrder = paymentGateway.findCardOrderResult(query);
        List<PaymentInfo.Card.Get> successPaymentInfo = cardOrder.paymentResults().stream()
                .filter(paymentResult -> paymentResult.status().equals("SUCCESS")).toList();
        if (successPaymentInfo.isEmpty()) {
            SucceedPaymentNotFound event = SucceedPaymentNotFound.of(orderId);
            paymentEventPublisher.publish(event);
            return;
        }
        if (successPaymentInfo.size() > 1) {
            PaymentSucceedDuplicated event = PaymentSucceedDuplicated.of(orderId);
            paymentEventPublisher.publish(event);
            return;
        }
        PaymentInfo.Card.Get paymentInfo = successPaymentInfo.get(0);
        PgPaymentCompleted event = PgPaymentCompleted.of(
                paymentInfo.orderId(),
                paymentInfo.transactionKey(),
                paymentInfo.status()
        );
        paymentEventPublisher.publish(event);
    }

    @Transactional
    public void failPointPayment(Long orderId, Long paymentId) {
        Payment payment = getPaymentWithLock(paymentId);
        payment.markFailed();
    }
}
