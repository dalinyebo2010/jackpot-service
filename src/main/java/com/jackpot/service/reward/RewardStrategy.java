package com.jackpot.service.reward;

import java.math.BigDecimal;

public interface RewardStrategy {
    String getName(); // e.g. "Fixed", "Variable"
    boolean isWinner(BigDecimal currentPool);
    BigDecimal calculateReward(BigDecimal currentPool);
}
