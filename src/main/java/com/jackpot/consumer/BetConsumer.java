package com.jackpot.consumer;

import com.jackpot.model.Bet;
import com.jackpot.service.JackpotContributionService;
import com.jackpot.service.JackpotRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BetConsumer {

    private static final Logger log = LoggerFactory.getLogger(BetConsumer.class);

    private final JackpotContributionService contributionService;
    private final JackpotRewardService rewardService;

    public BetConsumer(JackpotContributionService contributionService,
                       JackpotRewardService rewardService) {
        this.contributionService = contributionService;
        this.rewardService = rewardService;
    }

    @KafkaListener(topics = "jackpot-bets", groupId = "jackpot-service")
    public void consume(Bet bet) {
        contributionService.processContribution(bet)
                .doOnNext(contribution -> log.info(
                        "Contribution saved for bet {}: amount={}, percentage={}%, type={}",
                        bet.getId(),
                        contribution.getContributionAmount(),
                        contribution.getPercentageUsed(),
                        contribution.getStrategyType()
                ))
                .thenMany(rewardService.tryEvaluateReward(bet).flux())
                .doOnNext(reward -> log.info("Reward saved for bet {}: {}", bet.getId(), reward))
                .doOnError(err -> log.error("Error processing bet {}: {}", bet.getId(), err.getMessage()))
                .subscribe();
    }
}
