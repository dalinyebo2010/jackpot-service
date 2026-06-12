package com.jackpot.repository;

import com.jackpot.model.JackpotReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JackpotRewardRepository extends JpaRepository<JackpotReward, Long> {
    Optional<JackpotReward> findByBetId(Long betId);
}
