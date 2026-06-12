package com.jackpot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jackpot_reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long betId;
    private Long userId;
    private Long jackpotId;

    @Column(name = "reward_amount", nullable = false)
    private BigDecimal rewardAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

