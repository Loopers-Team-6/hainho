package com.loopers.domain.order.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
public class DiscountedPrice {
    private static final Long MIN_VALUE = 0L;

    @Column(name = "discounted_price")
    private final Long value;

    private DiscountedPrice(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("할인된 가격은 null일 수 없습니다.");
        }
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("할인된 가격은 " + MIN_VALUE + " 이상이어야 합니다.");
        }
        this.value = value;
    }

    public static DiscountedPrice of(Long value) {
        return new DiscountedPrice(value);
    }
}
