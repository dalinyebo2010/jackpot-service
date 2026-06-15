package com.jackpot.service.reward;

import java.math.BigDecimal;

public interface RewardStrategy {
    String getName(); // e.g. "FIXED", "VARIABLE"
    boolean isWinner(BigDecimal currentPool);
    BigDecimal calculateReward(BigDecimal currentPool);

    // for clarity
    BigDecimal getPercentage(BigDecimal currentPool); // percentage applied based on pool
    boolean isFixed();                                // true if fixed, false if variable
}
