package com.jackpot.service.reward;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedRewardStrategy implements RewardStrategy {

    // Injected from application.properties
    @Value("${jackpot.fixed.percentage}")
    private BigDecimal percentage; // default 20%

    @Value("${jackpot.fixed.threshold}")
    private BigDecimal threshold;  // default 5000

    @Override
    public String getName() {
        return "FIXED";
    }

    @Override
    public boolean isWinner(BigDecimal currentPool) {
        // Win if pool exceeds configured threshold
        return currentPool.compareTo(threshold) > 0;
    }

    @Override
    public BigDecimal calculateReward(BigDecimal currentPool) {
        // Reward is always configured percentage of pool
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
