package com.jackpot.service;

import com.jackpot.exception.JackpotNotFoundException;
import com.jackpot.exception.NoRewardException;
import com.jackpot.exception.StrategyNotFoundException;
import com.jackpot.model.Bet;
import com.jackpot.model.JackpotReward;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.repository.JackpotRewardRepository;
import com.jackpot.service.reward.RewardStrategy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JackpotRewardService {

    private final JackpotRepository jackpotRepo;
    private final JackpotRewardRepository rewardRepo;
    private final Map<String, RewardStrategy> strategies;

    public JackpotRewardService(JackpotRepository jackpotRepo,
                                JackpotRewardRepository rewardRepo,
                                List<RewardStrategy> strategyList) {
        this.jackpotRepo = jackpotRepo;
        this.rewardRepo = rewardRepo;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RewardStrategy::getName, s -> s));
    }

    public Mono<JackpotReward> evaluateReward(Bet bet) {
        return rewardRepo.findByBetId(bet.getId())
                .switchIfEmpty(
                        jackpotRepo.findById(bet.getJackpotId())
                                .switchIfEmpty(Mono.error(new JackpotNotFoundException(bet.getJackpotId())))
                                .flatMap(jackpot -> {
                                    RewardStrategy strategy = strategies.get(jackpot.getRewardStrategy());
                                    if (strategy == null) {
                                        return Mono.error(new StrategyNotFoundException("reward", jackpot.getRewardStrategy()));
                                    }

                                    // Use contribution percentage from jackpot entity
                                    BigDecimal contributionPercentage = jackpot.getContributionPercentage();
                                    BigDecimal contribution = bet.getAmount().multiply(contributionPercentage);
                                    jackpot.setCurrentPool(jackpot.getCurrentPool().add(contribution));

                                    if (strategy.isWinner(jackpot.getCurrentPool())) {
                                        BigDecimal rewardAmount = strategy.calculateReward(jackpot.getCurrentPool());

                                        JackpotReward jr = JackpotReward.builder()
                                                .betId(bet.getId())
                                                .userId(bet.getUserId())
                                                .jackpotId(bet.getJackpotId())
                                                .rewardAmount(rewardAmount)
                                                .percentageUsed(strategy.getPercentage(jackpot.getCurrentPool()))
                                                .strategyType(strategy.isFixed() ? "FIXED" : "VARIABLE")
                                                .build();

                                        return rewardRepo.save(jr)
                                                .flatMap(savedReward -> {
                                                    jackpot.setCurrentPool(jackpot.getInitialPool());
                                                    return jackpotRepo.save(jackpot).thenReturn(savedReward);
                                                });
                                    }
                                    return Mono.error(new NoRewardException(bet.getId()));
                                })
                );
    }

    public Mono<JackpotReward> tryEvaluateReward(Bet bet) {
        return jackpotRepo.findById(bet.getJackpotId())
                .switchIfEmpty(Mono.error(new JackpotNotFoundException(bet.getJackpotId())))
                .flatMap(jackpot -> {
                    RewardStrategy strategy = strategies.get(jackpot.getRewardStrategy());
                    if (strategy == null) {
                        return Mono.error(new StrategyNotFoundException("reward", jackpot.getRewardStrategy()));
                    }

                    if (strategy.isWinner(jackpot.getCurrentPool())) {
                        BigDecimal rewardAmount = strategy.calculateReward(jackpot.getCurrentPool());

                        JackpotReward jr = JackpotReward.builder()
                                .betId(bet.getId())
                                .userId(bet.getUserId())
                                .jackpotId(bet.getJackpotId())
                                .rewardAmount(rewardAmount)
                                .percentageUsed(strategy.getPercentage(jackpot.getCurrentPool()))
                                .strategyType(strategy.isFixed() ? "FIXED" : "VARIABLE")
                                .build();

                        return rewardRepo.save(jr)
                                .flatMap(savedReward -> {
                                    jackpot.setCurrentPool(jackpot.getInitialPool());
                                    return jackpotRepo.save(jackpot).thenReturn(savedReward);
                                });
                    }
                    return Mono.empty();
                });
    }
}
