package com.jackpot.service;

import com.jackpot.exception.BetNotFoundException;
import com.jackpot.model.Bet;
import com.jackpot.producer.BetProducer;
import com.jackpot.repository.BetRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BetService {

    private final BetRepository betRepository;
    private final JackpotContributionService contributionService;
    private final BetProducer betProducer;

    public BetService(BetRepository betRepository,
                      JackpotContributionService contributionService,
                      BetProducer betProducer) {
        this.betRepository = betRepository;
        this.contributionService = contributionService;
        this.betProducer = betProducer;
    }

    /**
     * Place a bet:
     * 1. Save the bet
     * 2. Process contribution (update jackpot pool)
     * 3. Publish bet to Kafka
     * 4. Return the saved bet
     */
    public Mono<Bet> placeBet(Bet bet) {
        return betRepository.save(bet)
                .flatMap(savedBet ->
                        contributionService.processContribution(savedBet)
                                .then(Mono.fromRunnable(() -> betProducer.publishBet(savedBet)))
                                .thenReturn(savedBet)
                );
    }

    /**
     * Find a bet by ID, or throw BetNotFoundException if missing.
     */
    public Mono<Bet> findBetById(Long betId) {
        return betRepository.findById(betId)
                .switchIfEmpty(Mono.error(new BetNotFoundException(betId)));
    }
}
