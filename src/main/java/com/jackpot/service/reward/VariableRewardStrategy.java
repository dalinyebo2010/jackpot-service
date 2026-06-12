package com.jackpot.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VariableRewardStrategy implements RewardStrategy {
    public boolean isWinner(BigDecimal currentPool) {
        double chance = currentPool.doubleValue() > 10000 ? 1.0 : currentPool.doubleValue() / 10000.0;
        return Math.random() < chance;
    }
    public BigDecimal calculateReward(BigDecimal currentPool) {
        return currentPool;
    }
}
