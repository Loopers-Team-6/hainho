package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ranking_weight")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RankingWeight extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private RankingTargetType type;

    @Enumerated(EnumType.STRING)
    private WeightType weightType;

    private Double weight;

    private RankingWeight(RankingTargetType type, WeightType weightType, double weight) {
        this.type = type;
        this.weightType = weightType;
        this.weight = weight;
    }

    public static RankingWeight of(RankingTargetType type, WeightType weightType, Double weight) {
        return new RankingWeight(type, weightType, weight);
    }
}
