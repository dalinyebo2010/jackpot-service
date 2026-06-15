package com.jackpot.exception;

public class JackpotNotFoundException extends RuntimeException {
    public JackpotNotFoundException(Long jackpotId) {
        super("Jackpot not found with id: " + jackpotId);
    }
}
