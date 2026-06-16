package com.jackpot.service.reward;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VariableRewardStrategy implements RewardStrategy {

    @Value("${jackpot.variable.divisor}")
    private BigDecimal divisor; // default 7

    @Value("${jackpot.variable.threshold}")
    private BigDecimal threshold; // default 5000

    @Value("${jackpot.variable.lowFactor:}")
    private BigDecimal lowFactor; // default 5%

    @Value("${jackpot.variable.highFactor}")
    private BigDecimal highFactor; // default 10%

    @Override
    public String getName() {
        return "VARIABLE";
    }

    @Override
    public boolean isWinner(BigDecimal currentPool) {
        // Win if pool is divisible by configured divisor
        return currentPool.remainder(divisor).compareTo(BigDecimal.ZERO) == 0;
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
        // Use configured threshold and factors
        return currentPool.compareTo(threshold) < 0 ? lowFactor : highFactor;
    }
}
