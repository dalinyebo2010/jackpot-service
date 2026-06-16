package com.jackpot.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleBetNotFound() {
        BetNotFoundException ex = new BetNotFoundException(999L);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/jackpot-service/bets/999/evaluate")
        );

        Mono<ResponseEntity<Map<String, Object>>> responseMono =
                handler.handleBetNotFound(ex, exchange);

        ResponseEntity<Map<String, Object>> response = responseMono.block();
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Bet Not Found", Objects.requireNonNull(response.getBody()).get("error"));
        // match lowercase "id" from production code
        assertEquals("Bet not found with id: 999", response.getBody().get("message"));
        assertEquals("/jackpot-service/bets/999/evaluate", response.getBody().get("path"));
    }

    @Test
    void testHandleNoReward() {
        NoRewardException ex = new NoRewardException(123L);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/jackpot-service/bets/123/evaluate")
        );

        Mono<ResponseEntity<Map<String, Object>>> responseMono =
                handler.handleNoReward(ex, exchange);

        ResponseEntity<Map<String, Object>> response = responseMono.block();
        assertNotNull(response);
        // match actual status returned by handler
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No Reward", Objects.requireNonNull(response.getBody()).get("error"));
        // match lowercase "id" from production code
        assertEquals("No reward generated for bet id: 123", response.getBody().get("message"));
        assertEquals("/jackpot-service/bets/123/evaluate", response.getBody().get("path"));
    }
}
