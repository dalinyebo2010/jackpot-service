package com.jackpot.service.rewardstrategy;

import com.jackpot.service.reward.FixedRewardStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // loads application.yml and injects values
class FixedRewardStrategyTest {

    @Autowired
    private FixedRewardStrategy strategy; // Spring will inject with configured percentage

    @Test
    void testIsWinnerTrue() {
        assertTrue(strategy.isWinner(BigDecimal.valueOf(6000)));
    }

    @Test
    void testIsWinnerFalse() {
        assertFalse(strategy.isWinner(BigDecimal.valueOf(4000)));
    }

    @Test
    void testCalculateReward() {
        BigDecimal reward = strategy.calculateReward(BigDecimal.valueOf(10000));
        assertEquals(BigDecimal.valueOf(2000.00).setScale(2), reward.setScale(2));
    }

    @Test
    void testGetPercentage() {
        BigDecimal percentage = strategy.getPercentage(BigDecimal.valueOf(10000));
        // use compareTo to ignore scale differences (20 vs 20.0)
        assertEquals(0, percentage.compareTo(BigDecimal.valueOf(20)));
    }
}
