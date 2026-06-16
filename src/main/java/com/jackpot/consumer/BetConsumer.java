package com.jackpot.consumer;

import com.jackpot.model.Bet;
import com.jackpot.service.JackpotContributionService;
import com.jackpot.service.JackpotRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
        if (bet.getId() == null) {
            log.error("Received bet without ID, cannot process: {}", bet);
            return;
        }

        contributionService.processContribution(bet)
                .doOnNext(contribution -> log.info(
                        "Contribution saved for bet {}: amount={}, percentage={}%, type={}",
                        bet.getId(),
                        contribution.getContributionAmount(),
                        contribution.getPercentageUsed(),
                        contribution.getStrategyType()
                ))
                .flatMap(contribution ->
                        rewardService.tryEvaluateReward(bet)
                                .doOnNext(reward -> log.info("Reward saved for bet {}: {}", bet.getId(), reward))
                                .switchIfEmpty(Mono.fromRunnable(() ->
                                        // Treat no reward as normal outcome, not error
                                        log.info("No reward generated for bet {}", bet.getId())
                                ))
                )
                .onErrorResume(err -> {
                    // Only log genuine errors (DB, missing jackpot, etc.)
                    log.error("Error processing bet {}: {}", bet.getId(), err.getMessage(), err);
                    return Mono.empty();
                })
                .subscribe();
    }
}
