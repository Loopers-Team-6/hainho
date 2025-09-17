package com.loopers.domain.coupon;

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
public class CouponService {
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final CouponRepository couponRepository;
    private final CouponEventPublisher couponEventPublisher;

    @Transactional
    public Long useCoupon(Long couponIssuanceId, Long orderId, Long orderPrice) {
        if (couponIssuanceId == null) {
            return 0L;
        }
        CouponIssuance couponIssuance = getCouponIssuance(couponIssuanceId);

        Long couponId = couponIssuance.getCouponId();
        Coupon coupon = getCoupon(couponId);
        Long discountAmount = coupon.calculateDiscountAmount(orderPrice);

        couponIssuance.use(orderId);

        CouponUsed couponUsed = new CouponUsed(couponId, orderId, orderPrice);
        couponEventPublisher.publish(couponUsed);

        return discountAmount;
    }

    private CouponIssuance getCouponIssuance(Long couponIssuanceId) {
        return couponIssuanceRepository.findById(couponIssuanceId)
                .orElseThrow(() -> new EntityNotFoundException("쿠폰 발급 정보를 찾을 수 없습니다. 쿠폰 발급 ID: " + couponIssuanceId));
    }

    private Coupon getCoupon(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("쿠폰 정보를 찾을 수 없습니다. 쿠폰 ID: " + couponId));
    }

    @Transactional
    public void revertCoupons(Long orderId) {
        List<CouponIssuance> couponIssuance = couponIssuanceRepository.findAllByUsedOrderId(orderId);
        couponIssuance.forEach(this::revertCoupon);
    }

    private void revertCoupon(CouponIssuance couponIssuance) {
        try {
            couponIssuance.revert();
        } catch (Exception e) {
            log.warn("쿠폰 취소 처리 중 오류가 발생했습니다. 쿠폰 발급 ID: {}", couponIssuance.getId(), e);
        }
    }
}
