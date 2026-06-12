package com.jackpot.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedRewardStrategy implements RewardStrategy {
    private final double chance = 0.05; // 5%
    public boolean isWinner(BigDecimal currentPool) {
        return Math.random() < chance;
    }
    public BigDecimal calculateReward(BigDecimal currentPool) {
        return currentPool.multiply(new BigDecimal("0.5"));
    }
}
