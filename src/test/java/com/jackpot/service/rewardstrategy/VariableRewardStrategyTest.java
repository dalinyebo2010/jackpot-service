package com.jackpot.service.rewardstrategy;

import com.jackpot.service.reward.VariableRewardStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // loads application.yml and injects values
class VariableRewardStrategyTest {

    @Autowired
    private VariableRewardStrategy strategy; // Spring injects with configured divisor/factors

    @Test
    void testIsWinnerDivisibleBy7() {
        assertTrue(strategy.isWinner(BigDecimal.valueOf(7000)));
    }

    @Test
    void testIsWinnerNotDivisibleBy7() {
        assertFalse(strategy.isWinner(BigDecimal.valueOf(7010)));
    }

    @Test
    void testCalculateRewardLowFactor() {
        BigDecimal reward = strategy.calculateReward(BigDecimal.valueOf(4000));
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), reward.setScale(2)); // 5%
    }

    @Test
    void testCalculateRewardHighFactor() {
        BigDecimal reward = strategy.calculateReward(BigDecimal.valueOf(6000));
        assertEquals(BigDecimal.valueOf(600.00).setScale(2), reward.setScale(2)); // 10%
    }

    @Test
    void testGetPercentageLowPool() {
        BigDecimal percentage = strategy.getPercentage(BigDecimal.valueOf(4000));
        // use compareTo to ignore scale differences
        assertEquals(0, percentage.compareTo(BigDecimal.valueOf(5)));
    }

    @Test
    void testGetPercentageHighPool() {
        BigDecimal percentage = strategy.getPercentage(BigDecimal.valueOf(6000));
        // use compareTo to ignore scale differences
        assertEquals(0, percentage.compareTo(BigDecimal.valueOf(10)));
    }
}
