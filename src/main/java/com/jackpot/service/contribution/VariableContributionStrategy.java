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
        BigDecimal factor = currentPool.compareTo(new BigDecimal("10000")) < 0
                ? new BigDecimal("0.10") : new BigDecimal("0.02");
        return betAmount.multiply(factor);
    }
}
