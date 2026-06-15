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
        return currentPool.remainder(BigDecimal.valueOf(7)).equals(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateReward(BigDecimal currentPool) {
        BigDecimal factor = getFactor(currentPool);
        return currentPool.multiply(factor);
    }

    @Override
    public BigDecimal getPercentage(BigDecimal currentPool) {
        BigDecimal factor = getFactor(currentPool);
        return factor.multiply(BigDecimal.valueOf(100)); // convert to %
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    private BigDecimal getFactor(BigDecimal currentPool) {
        return currentPool.compareTo(new BigDecimal("5000")) < 0
                ? new BigDecimal("0.50")   // 50% if pool < 5000
                : new BigDecimal("0.10");  // 10% otherwise
    }
}
