package com.jackpot.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VariableRewardStrategy implements RewardStrategy {

    @Override
    public String getName() {
        return "VARIABLE";
    }

    @Override
    public boolean isWinner(BigDecimal currentPool) {
        // Example: win if pool is divisible by 7
        return currentPool.remainder(new BigDecimal("7")).equals(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateReward(BigDecimal currentPool) {
        // Example: reward is 20% of current pool
        return currentPool.multiply(new BigDecimal("0.20"));
    }
}
