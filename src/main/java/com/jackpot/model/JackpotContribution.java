package com.jackpot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot_contribution")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotContribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long betId;
    private Long userId;
    private Long jackpotId;

    @Column(name = "stake_amount", nullable = false)
    private BigDecimal stakeAmount;

    @Column(name = "contribution_amount", nullable = false)
    private BigDecimal contributionAmount;

    @Column(name = "current_jackpot_amount", nullable = false)
    private BigDecimal currentJackpotAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

