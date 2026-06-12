package com.jackpot.repository;

import com.jackpot.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByJackpotId(Long jackpotId);
}
