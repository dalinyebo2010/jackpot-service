package com.jackpot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jackpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "initial_pool", nullable = false)
    private BigDecimal initialPool;

    @Column(name = "current_pool", nullable = false)
    private BigDecimal currentPool;

    @Column(name = "contribution_strategy", nullable = false)
    private String contributionStrategy; // FIXED, VARIABLE

    @Column(name = "reward_strategy", nullable = false)
    private String rewardStrategy; // FIXED, VARIABLE

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}

