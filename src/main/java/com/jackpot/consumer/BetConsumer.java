package com.jackpot.consumer;

import com.jackpot.model.Bet;
import com.jackpot.service.JackpotContributionService;
import com.jackpot.service.JackpotRewardService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BetConsumer {
    private final JackpotContributionService contributionService;
    private final JackpotRewardService rewardService;

    public BetConsumer(JackpotContributionService contributionService, JackpotRewardService rewardService) {
        this.contributionService = contributionService;
        this.rewardService = rewardService;
    }

    @KafkaListener(topics = "jackpot-bets", groupId = "jackpot-service")
    public void consume(Bet bet) {
        contributionService.processContribution(bet);
        rewardService.evaluateReward(bet);
    }
}

