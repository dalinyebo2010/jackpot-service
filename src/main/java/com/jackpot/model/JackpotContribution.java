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
@Table("jackpot_contribution")   // R2DBC mapping
public class JackpotContribution {

    @Id
    private Long id;   // Auto-increment handled in schema (data.sql)

    @Column("bet_id")
    private Long betId;

    @Column("user_id")
    private Long userId;

    @Column("jackpot_id")
    private Long jackpotId;

    @Column("stake_amount")
    private BigDecimal stakeAmount;

    @Column("contribution_amount")
    private BigDecimal contributionAmount;

    @Column("current_jackpot_amount")
    private BigDecimal currentJackpotAmount;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // for clarity
    @Column("percentage_used")
    private BigDecimal percentageUsed;   // e.g. 10% contribution

    @Column("strategy_type")
    private String strategyType;         // "FIXED" or "VARIABLE"
}
