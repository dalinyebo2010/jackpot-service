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
        kafkaTemplate.send("jackpot-bets", bet.getId().toString(), bet);
    }
}

