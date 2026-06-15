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
@Table("jackpot")   // R2DBC mapping annotation
public class Jackpot {

    @Id
    private Long id;   // R2DBC does not support GenerationType.IDENTITY; use auto-increment in schema

    private String name;

    @Column("initial_pool")
    private BigDecimal initialPool;

    @Column("current_pool")
    private BigDecimal currentPool;

    @Column("contribution_strategy")
    private String contributionStrategy; // FIXED, VARIABLE

    @Column("reward_strategy")
    private String rewardStrategy; // FIXED, VARIABLE

    @Column("contribution_percentage")
    private BigDecimal contributionPercentage; // e.g. 0.05 for FIXED, 0.10 for VARIABLE

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
