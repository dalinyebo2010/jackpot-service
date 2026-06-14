package com.jackpot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("bets")   // <-- match your actual table name
public class Bet {

    @Id
    @Column("id")   // <-- match your actual column name
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("jackpot_id")
    private Long jackpotId;

    @Column("amount")
    private BigDecimal amount;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
