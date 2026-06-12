package com.jackpot.service;

import com.jackpot.model.Bet;
import com.jackpot.model.Jackpot;
import com.jackpot.model.JackpotContribution;
import com.jackpot.repository.JackpotContributionRepository;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.service.contribution.ContributionStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JackpotContributionService {
    private final JackpotRepository jackpotRepo;
    private final JackpotContributionRepository contributionRepo;
    private final Map<String, ContributionStrategy> strategies;

    public JackpotContributionService(JackpotRepository jackpotRepo,
                                      JackpotContributionRepository contributionRepo,
                                      List<ContributionStrategy> strategyList) {
        this.jackpotRepo = jackpotRepo;
        this.contributionRepo = contributionRepo;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(s -> s.getClass().getSimpleName(), s -> s));
    }

    @Transactional
    public void processContribution(Bet bet) {
        Jackpot jackpot = jackpotRepo.findById(bet.getJackpotId())
                .orElseThrow(() -> new IllegalArgumentException("Jackpot not found"));

        ContributionStrategy strategy = strategies.get(jackpot.getContributionStrategy() + "ContributionStrategy");
        BigDecimal contribution = strategy.calculateContribution(bet.getAmount(), jackpot.getCurrentPool());

        jackpot.setCurrentPool(jackpot.getCurrentPool().add(contribution));
        jackpotRepo.save(jackpot);

        JackpotContribution jc = JackpotContribution.builder()
                .betId(bet.getId())
                .userId(bet.getUserId())
                .jackpotId(bet.getJackpotId())
                .stakeAmount(bet.getAmount())
                .contributionAmount(contribution)
                .currentJackpotAmount(jackpot.getCurrentPool())
                .build();

        contributionRepo.save(jc);
    }
}

