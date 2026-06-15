package com.jackpot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BetNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBetNotFound(
            BetNotFoundException ex, ServerWebExchange exchange) {
        return Mono.just(buildErrorResponse(HttpStatus.NOT_FOUND, "Bet Not Found", ex.getMessage(), exchange));
    }

    @ExceptionHandler(JackpotNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleJackpotNotFound(
            JackpotNotFoundException ex, ServerWebExchange exchange) {
        return Mono.just(buildErrorResponse(HttpStatus.NOT_FOUND, "Jackpot Not Found", ex.getMessage(), exchange));
    }

    @ExceptionHandler(StrategyNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleStrategyNotFound(
            StrategyNotFoundException ex, ServerWebExchange exchange) {
        return Mono.just(buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Strategy", ex.getMessage(), exchange));
    }

    @ExceptionHandler(NoRewardException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoReward(
            NoRewardException ex, ServerWebExchange exchange) {
        return Mono.just(buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "No Reward",
                ex.getMessage(),
                exchange
        ));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGeneric(
            Exception ex, ServerWebExchange exchange) {
        return Mono.just(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), exchange));
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message, ServerWebExchange exchange) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("path", exchange.getRequest().getPath().value());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("requestId", UUID.randomUUID().toString().substring(0, 8));

        return ResponseEntity.status(status).body(body);
    }
}

