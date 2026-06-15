package com.jackpot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("jackpot_reward")   // R2DBC mapping
public class JackpotReward {

    @Id
    private Long id;

    @Column("bet_id")
    private Long betId;

    @Column("user_id")
    private Long userId;

    @Column("jackpot_id")
    private Long jackpotId;

    @Column("reward_amount")
    private BigDecimal rewardAmount;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // fields for clarity
    @Column("percentage_used")
    private BigDecimal percentageUsed;   // e.g. 20% reward

    @Column("strategy_type")
    private String strategyType;         // "FIXED" or "VARIABLE"
}
