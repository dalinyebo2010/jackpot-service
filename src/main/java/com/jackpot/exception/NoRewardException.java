package com.jackpot.exception;

public class NoRewardException extends RuntimeException {
    public NoRewardException(Long betId) {
        super("No reward generated for bet id: " + betId);
    }
}
