package com.jackpot.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedRewardStrategy implements RewardStrategy {

    @Override
    public String getName() {
        return "FIXED";
    }

    @Override
    public boolean isWinner(BigDecimal currentPool) {
        // Example: win if pool exceeds 5000
        return currentPool.compareTo(new BigDecimal("5000")) > 0;
    }

    @Override
    public BigDecimal calculateReward(BigDecimal currentPool) {
        // Example: fixed reward of 1000
        return new BigDecimal("1000");
    }
}
