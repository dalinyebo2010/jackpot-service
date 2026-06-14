package com.jackpot.repository;

import com.jackpot.model.Jackpot;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface JackpotRepository extends ReactiveCrudRepository<Jackpot, Long> {

    // Removed: Flux<Jackpot> findByStatus(String status);

    // Example alternative queries you can keep:
    Flux<Jackpot> findByName(String name);

    Mono<Jackpot> findByRewardStrategy(String rewardStrategy);
}
