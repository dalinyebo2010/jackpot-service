package com.jackpot.repository;

import com.jackpot.model.JackpotContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JackpotContributionRepository extends JpaRepository<JackpotContribution, Long> {
    List<JackpotContribution> findByJackpotId(Long jackpotId);
}
