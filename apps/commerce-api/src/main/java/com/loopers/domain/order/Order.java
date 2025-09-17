package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.vo.DiscountedPrice;
import com.loopers.domain.order.vo.TotalPrice;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {
    private Long userId;
    @Embedded
    private TotalPrice totalPrice;
    @Embedded
    private DiscountedPrice discountedPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Version
    private Long version;

    private Order(Long userId, TotalPrice totalPrice, DiscountedPrice discountedPrice) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "userId는 null일 수 없습니다.");
        }
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.discountedPrice = discountedPrice;
        this.status = OrderStatus.PENDING;
    }

    public static Order create(Long userId, Long totalPrice) {
        return new Order(userId, TotalPrice.of(totalPrice), DiscountedPrice.of(totalPrice));
    }

    public void applyDiscount(Long discountingAmount) {
        if (discountingAmount == null) {
            throw new IllegalArgumentException("할인 금액은 null일 수 없습니다.");
        }
        if (discountingAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }
        Long discountedPriceValue = totalPrice.getValue() - discountingAmount;
        this.discountedPrice = DiscountedPrice.of(discountedPriceValue);
    }

    public void markProcessing() {
        if (!status.isProcessable()) {
            throw new IllegalStateException("주문이 처리 가능한 상태가 아닙니다. 현재 상태: " + status);
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void markCompleted() {
        if (!status.isCompletable()) {
            throw new IllegalStateException("주문이 완료 가능한 상태가 아닙니다. 현재 상태: " + status);
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void markCancelled() {
        if (!status.isCancellable()) {
            throw new IllegalStateException("주문이 취소 가능한 상태가 아닙니다. 현재 상태: " + status);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void markPending() {
        if (!status.isPendable()) {
            throw new IllegalStateException("주문이 대기 가능한 상태가 아닙니다. 현재 상태: " + status);
        }
        this.status = OrderStatus.PENDING;
    }
}
