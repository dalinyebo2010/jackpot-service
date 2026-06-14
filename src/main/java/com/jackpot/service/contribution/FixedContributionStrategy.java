package com.jackpot.service.contribution;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedContributionStrategy implements ContributionStrategy {

    private final BigDecimal percentage = new BigDecimal("0.05"); // 5%

    @Override
    public String getName() {
        return "FIXED";
    }

    @Override
    public BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool) {
        return betAmount.multiply(percentage);
    }
}
