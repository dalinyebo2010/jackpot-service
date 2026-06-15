package com.jackpot.service;

import com.jackpot.exception.JackpotNotFoundException;
import com.jackpot.exception.StrategyNotFoundException;
import com.jackpot.model.Bet;
import com.jackpot.model.JackpotContribution;
import com.jackpot.repository.JackpotContributionRepository;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.service.contribution.ContributionStrategy;
import com.jackpot.service.contribution.VariableContributionStrategy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .collect(Collectors.toMap(ContributionStrategy::getName, s -> s));
    }

    public Mono<JackpotContribution> processContribution(Bet bet) {
        return jackpotRepo.findById(bet.getJackpotId())
                .switchIfEmpty(Mono.error(new JackpotNotFoundException(bet.getJackpotId())))
                .flatMap(jackpot -> {
                    ContributionStrategy strategy = strategies.get(jackpot.getContributionStrategy());
                    if (strategy == null) {
                        return Mono.error(new StrategyNotFoundException("contribution", jackpot.getContributionStrategy()));
                    }

                    // Calculate contribution based on strategy
                    BigDecimal contribution = strategy.calculateContribution(bet.getAmount(), jackpot.getCurrentPool());
                    jackpot.setCurrentPool(jackpot.getCurrentPool().add(contribution));

                    // Determine percentage used dynamically
                    BigDecimal percentageUsed;
                    if (strategy instanceof VariableContributionStrategy) {
                        percentageUsed = ((VariableContributionStrategy) strategy)
                                .getPercentage(jackpot.getCurrentPool());
                    } else {
                        percentageUsed = strategy.getPercentage();
                    }

                    // Build contribution record
                    JackpotContribution jc = JackpotContribution.builder()
                            .betId(bet.getId())
                            .userId(bet.getUserId())
                            .jackpotId(bet.getJackpotId())
                            .stakeAmount(bet.getAmount())
                            .contributionAmount(contribution)
                            .currentJackpotAmount(jackpot.getCurrentPool())
                            .percentageUsed(percentageUsed)
                            .strategyType(strategy.isFixed() ? "FIXED" : "VARIABLE")
                            .build();

                    return jackpotRepo.save(jackpot)
                            .then(contributionRepo.save(jc));
                });
    }
}
