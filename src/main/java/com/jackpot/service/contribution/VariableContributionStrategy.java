package com.jackpot.service.contribution;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VariableContributionStrategy implements ContributionStrategy {

    @Override
    public String getName() {
        return "VARIABLE";
    }

    @Override
    public BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool) {
        BigDecimal factor = getFactor(currentPool);
        return betAmount.multiply(factor);
    }

    @Override
    public BigDecimal getPercentage() {
        // Return percentage as a whole number (e.g. 10 for 10%)
        // This method should be called with the current pool context
        // so we’ll expose a helper that calculates based on pool size
        throw new UnsupportedOperationException("Use getPercentage(currentPool) instead");
    }

    // helper method to calculate percentage dynamically
    public BigDecimal getPercentage(BigDecimal currentPool) {
        BigDecimal factor = getFactor(currentPool);
        return factor.multiply(BigDecimal.valueOf(100)); // convert to %
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    // Internal helper to decide factor based on pool size
    private BigDecimal getFactor(BigDecimal currentPool) {
        return currentPool.compareTo(new BigDecimal("10000")) < 0
                ? new BigDecimal("0.10")   // 10% if pool < 10,000
                : new BigDecimal("0.02");  // 2% otherwise
    }
}
