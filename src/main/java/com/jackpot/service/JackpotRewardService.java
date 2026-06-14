package com.jackpot.service;

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
        // Use strategy.getName() instead of class name
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RewardStrategy::getName, s -> s));
    }

    public Mono<JackpotReward> evaluateReward(Bet bet) {
        return jackpotRepo.findById(bet.getJackpotId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Jackpot not found")))
                .flatMap(jackpot -> {
                    RewardStrategy strategy = strategies.get(jackpot.getRewardStrategy());
                    if (strategy == null) {
                        return Mono.error(new IllegalArgumentException(
                                "Unknown reward strategy: " + jackpot.getRewardStrategy()));
                    }

                    if (strategy.isWinner(jackpot.getCurrentPool())) {
                        BigDecimal rewardAmount = strategy.calculateReward(jackpot.getCurrentPool());

                        JackpotReward jr = JackpotReward.builder()
                                .betId(bet.getId())
                                .userId(bet.getUserId())
                                .jackpotId(bet.getJackpotId())
                                .rewardAmount(rewardAmount)
                                .build();

                        return rewardRepo.save(jr)
                                .flatMap(savedReward -> {
                                    jackpot.setCurrentPool(jackpot.getInitialPool());
                                    return jackpotRepo.save(jackpot).thenReturn(savedReward);
                                });
                    }
                    return Mono.empty(); // no reward
                })
                .doOnError(err -> System.err.println("Error evaluating reward: " + err.getMessage()));
    }

    public Mono<JackpotReward> findRewardByBetId(Long betId) {
        return rewardRepo.findByBetId(betId);
    }
}
