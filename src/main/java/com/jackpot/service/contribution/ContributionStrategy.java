package com.jackpot.service.contribution;

import java.math.BigDecimal;

public interface ContributionStrategy {
    String getName(); // e.g. "Fixed", "Variable"
    BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool);
}
