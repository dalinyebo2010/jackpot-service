package com.jackpot.repository;

import com.jackpot.model.JackpotReward;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface JackpotRewardRepository extends ReactiveCrudRepository<JackpotReward, Long> {
    Mono<JackpotReward> findByBetId(Long betId);
}

