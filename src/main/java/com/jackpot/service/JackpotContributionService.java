package com.jackpot.service;

import com.jackpot.model.Bet;
import com.jackpot.model.JackpotContribution;
import com.jackpot.repository.JackpotContributionRepository;
import com.jackpot.repository.JackpotRepository;
import com.jackpot.service.contribution.ContributionStrategy;
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
        // Use strategy.getName() instead of class name
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ContributionStrategy::getName, s -> s));
    }

    public Mono<Void> processContribution(Bet bet) {
        return jackpotRepo.findById(bet.getJackpotId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Jackpot not found")))
                .flatMap(jackpot -> {
                    ContributionStrategy strategy = strategies.get(jackpot.getContributionStrategy());
                    if (strategy == null) {
                        return Mono.error(new IllegalArgumentException(
                                "Unknown contribution strategy: " + jackpot.getContributionStrategy()));
                    }

                    BigDecimal contribution = strategy.calculateContribution(bet.getAmount(), jackpot.getCurrentPool());
                    jackpot.setCurrentPool(jackpot.getCurrentPool().add(contribution));

                    JackpotContribution jc = JackpotContribution.builder()
                            .betId(bet.getId())
                            .userId(bet.getUserId())
                            .jackpotId(bet.getJackpotId())
                            .stakeAmount(bet.getAmount())
                            .contributionAmount(contribution)
                            .currentJackpotAmount(jackpot.getCurrentPool())
                            .build();

                    // Save jackpot update, then save contribution
                    return jackpotRepo.save(jackpot)
                            .then(contributionRepo.save(jc))
                            .then();
                });
    }
}
