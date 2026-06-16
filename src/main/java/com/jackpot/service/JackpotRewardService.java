package com.jackpot.service;

import com.jackpot.exception.JackpotNotFoundException;
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

    /**
     * Evaluate reward when explicitly requested (e.g. via controller).
     * Uses the already updated pool from contribution service.
     */
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

                                    BigDecimal currentPool = jackpot.getCurrentPool();

                                    if (strategy.isWinner(currentPool)) {
                                        BigDecimal rewardAmount = strategy.calculateReward(currentPool);

                                        JackpotReward jr = JackpotReward.builder()
                                                .betId(bet.getId())
                                                .userId(bet.getUserId())
                                                .jackpotId(bet.getJackpotId())
                                                .rewardAmount(rewardAmount)
                                                .percentageUsed(strategy.getPercentage(currentPool))
                                                .strategyType(strategy.isFixed() ? "FIXED" : "VARIABLE")
                                                .build();

                                        return rewardRepo.save(jr)
                                                .flatMap(savedReward -> {
                                                    // Reset pool only after a win
                                                    jackpot.setCurrentPool(jackpot.getInitialPool());
                                                    return jackpotRepo.save(jackpot).thenReturn(savedReward);
                                                });
                                    }

                                    // No reward → return empty instead of error
                                    return Mono.empty();
                                })
                );
    }

    /**
     * Try to evaluate reward immediately after contribution.
     * Same logic as evaluateReward, but called from consumer.
     */
    public Mono<JackpotReward> tryEvaluateReward(Bet bet) {
        return jackpotRepo.findById(bet.getJackpotId())
                .switchIfEmpty(Mono.error(new JackpotNotFoundException(bet.getJackpotId())))
                .flatMap(jackpot -> {
                    RewardStrategy strategy = strategies.get(jackpot.getRewardStrategy());
                    if (strategy == null) {
                        return Mono.error(new StrategyNotFoundException("reward", jackpot.getRewardStrategy()));
                    }

                    BigDecimal currentPool = jackpot.getCurrentPool();

                    if (strategy.isWinner(currentPool)) {
                        BigDecimal rewardAmount = strategy.calculateReward(currentPool);

                        JackpotReward jr = JackpotReward.builder()
                                .betId(bet.getId())
                                .userId(bet.getUserId())
                                .jackpotId(bet.getJackpotId())
                                .rewardAmount(rewardAmount)
                                .percentageUsed(strategy.getPercentage(currentPool))
                                .strategyType(strategy.isFixed() ? "FIXED" : "VARIABLE")
                                .build();

                        return rewardRepo.save(jr)
                                .flatMap(savedReward -> {
                                    // Reset pool after reward
                                    jackpot.setCurrentPool(jackpot.getInitialPool());
                                    return jackpotRepo.save(jackpot).thenReturn(savedReward);
                                });
                    }

                    // No reward is a normal outcome → return empty instead of error
                    return Mono.empty();
                });
    }
}
