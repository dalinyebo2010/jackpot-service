package com.jackpot.repository;

import com.jackpot.model.JackpotContribution;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface JackpotContributionRepository extends ReactiveCrudRepository<JackpotContribution, Long> {

    // Example: find all contributions for a given bet
    Flux<JackpotContribution> findByBetId(Long betId);

    // Example: find all contributions for a specific jackpot
    Flux<JackpotContribution> findByJackpotId(Long jackpotId);

    // Example: find a single contribution by user and bet
    Mono<JackpotContribution> findByUserIdAndBetId(Long userId, Long betId);
}
