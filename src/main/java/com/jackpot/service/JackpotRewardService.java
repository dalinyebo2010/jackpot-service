package com.jackpot.service;

import com.jackpot.model.Bet;
import com.jackpot.model.Jackpot;
import com.jackpot.model.JackpotReward;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.repository.JackpotRewardRepository;
import com.jackpot.service.reward.RewardStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                .collect(Collectors.toMap(s -> s.getClass().getSimpleName(), s -> s));
    }

    @Transactional
    public void evaluateReward(Bet bet) {
        Jackpot jackpot = jackpotRepo.findById(bet.getJackpotId())
                .orElseThrow(() -> new IllegalArgumentException("Jackpot not found"));

        RewardStrategy strategy = strategies.get(jackpot.getRewardStrategy() + "RewardStrategy");
        if (strategy.isWinner(jackpot.getCurrentPool())) {
            BigDecimal rewardAmount = strategy.calculateReward(jackpot.getCurrentPool());

            JackpotReward jr = JackpotReward.builder()
                    .betId(bet.getId())
                    .userId(bet.getUserId())
                    .jackpotId(bet.getJackpotId())
                    .rewardAmount(rewardAmount)
                    .build();

            rewardRepo.save(jr);

            jackpot.setCurrentPool(jackpot.getInitialPool());
            jackpotRepo.save(jackpot);
        }
    }

    public Optional<JackpotReward> findRewardByBetId(Long betId) {
        return rewardRepo.findByBetId(betId);
    }
}

