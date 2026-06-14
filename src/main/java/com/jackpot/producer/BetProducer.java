package com.jackpot.producer;

import com.jackpot.model.Bet;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BetProducer {

    private final KafkaTemplate<String, Bet> kafkaTemplate;

    public BetProducer(KafkaTemplate<String, Bet> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBet(Bet bet) {
        // Use bet ID if available, otherwise generate a UUID
        String key;
        if (bet.getId() != null) {
            key = bet.getId().toString();
        } else {
            key = UUID.randomUUID().toString();
            bet.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        }

        kafkaTemplate.send("jackpot-bets", key, bet);
    }
}
