package com.jackpot.producer;

import com.jackpot.model.Bet;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BetProducer {

    private final KafkaTemplate<String, Bet> kafkaTemplate;

    public BetProducer(KafkaTemplate<String, Bet> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBet(Bet bet) {
        // Always use the database-assigned bet ID as the Kafka key
        if (bet.getId() == null) {
            throw new IllegalArgumentException("Bet ID must not be null when publishing");
        }

        String key = bet.getId().toString();
        kafkaTemplate.send("jackpot-bets", key, bet);
    }
}