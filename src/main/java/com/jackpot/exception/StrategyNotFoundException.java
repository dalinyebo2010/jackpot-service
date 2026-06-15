package com.jackpot.exception;

public class StrategyNotFoundException extends RuntimeException {
    public StrategyNotFoundException(String strategyType, String strategyName) {
        super("Unknown " + strategyType + " strategy: " + strategyName);
    }
}
