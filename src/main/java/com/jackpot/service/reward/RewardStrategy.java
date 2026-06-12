package com.jackpot.service.reward;

import java.math.BigDecimal;

public interface RewardStrategy {
    boolean isWinner(BigDecimal currentPool);
    BigDecimal calculateReward(BigDecimal currentPool);
}
