package com.jackpot.exception;

public class BetNotFoundException extends RuntimeException {
    public BetNotFoundException(Long betId) {
        super("Bet not found with id: " + betId);
    }
}
