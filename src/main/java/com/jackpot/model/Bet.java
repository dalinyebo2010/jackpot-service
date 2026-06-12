package com.jackpot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data                   // generates getters, setters, equals, hashCode, toString
@NoArgsConstructor      // generates a no-args constructor
@AllArgsConstructor     // generates an all-args constructor
@Builder                // enables builder pattern
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long jackpotId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}

