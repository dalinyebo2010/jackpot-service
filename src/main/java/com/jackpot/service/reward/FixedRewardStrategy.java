package com.jackpot.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedRewardStrategy implements RewardStrategy {

    private final BigDecimal percentage = new BigDecimal("0.20"); // 20%

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
        // Reward is always 20% of pool
        return currentPool.multiply(percentage);
    }

    @Override
    public BigDecimal getPercentage(BigDecimal currentPool) {
        return percentage.multiply(BigDecimal.valueOf(100)); // return as % (e.g. 20)
    }

    @Override
    public boolean isFixed() {
        return true;
    }
}
