package com.jackpot.service.contribution;

import java.math.BigDecimal;

public interface ContributionStrategy {
    String getName(); // e.g. "FIXED", "VARIABLE"
    BigDecimal calculateContribution(BigDecimal betAmount, BigDecimal currentPool);

    BigDecimal getPercentage();   // percentage used for calculation (e.g. 0.05 for 5%)
    boolean isFixed();            // true if fixed strategy, false if variable
}
