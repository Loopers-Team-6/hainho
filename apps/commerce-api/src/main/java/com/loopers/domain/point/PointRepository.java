package com.loopers.domain.point;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(Long userId);

    Optional<Point> findByUserIdWithLock(Long userId);

    Point save(Point point);

    boolean existsByUserId(Long userId);
}
