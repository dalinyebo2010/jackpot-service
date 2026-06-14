package com.jackpot.repository;

import com.jackpot.model.Bet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BetRepository extends ReactiveCrudRepository<Bet, Long> {

    // Find all bets by user
    Flux<Bet> findByUserId(Long userId);

    // Find all bets for a jackpot
    Flux<Bet> findByJackpotId(Long jackpotId);
}
