package com.jackpot.controller;

import com.jackpot.exception.BetNotFoundException;
import com.jackpot.exception.NoRewardException;
import com.jackpot.model.Bet;
import com.jackpot.model.JackpotReward;
import com.jackpot.producer.BetProducer;
import com.jackpot.repository.BetRepository;
import com.jackpot.service.BetService;
import com.jackpot.service.JackpotRewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bets")
public class BetController {

    private static final Logger log = LoggerFactory.getLogger(BetController.class);

    private final BetService betService;
    private final BetProducer betProducer;
    private final JackpotRewardService rewardService;
    private final BetRepository betRepository;

    public BetController(BetService betService,
                         BetProducer betProducer,
                         JackpotRewardService rewardService,
                         BetRepository betRepository) {
        this.betService = betService;
        this.betProducer = betProducer;
        this.rewardService = rewardService;
        this.betRepository = betRepository;
    }

    /**
     * Place a bet:
     * - Saves bet
     * - Processes contribution
     * - Publishes to Kafka
     */
    @PostMapping
    public Mono<ResponseEntity<String>> publishBet(@RequestBody Bet bet) {
        return betService.placeBet(bet)
                .flatMap(savedBet -> {
                    // Publish the saved bet with its actual DB id
                    betProducer.publishBet(savedBet);
                    return Mono.just(ResponseEntity.ok(
                            "Bet published with ID: " + savedBet.getId()
                    ));
                });
    }

    @GetMapping("/{id}/evaluate")
    public Mono<ResponseEntity<Map<String, Object>>> evaluate(@PathVariable Long id) {
        return betRepository.findById(id)
                .flatMap(bet -> rewardService.evaluateReward(bet)
                        .map(reward -> {
                            Map<String, Object> response = new HashMap<>();
                            response.put("betId", reward.getBetId());
                            response.put("jackpotId", reward.getJackpotId());
                            response.put("winner", true);
                            response.put("rewardAmount", reward.getRewardAmount());
                            response.put("percentageUsed", reward.getPercentageUsed());
                            response.put("strategyType", reward.getStrategyType());
                            return ResponseEntity.ok(response);
                        })
                        .onErrorResume(NoRewardException.class, ex -> {
                            Map<String, Object> response = new HashMap<>();
                            response.put("betId", bet.getId());
                            response.put("jackpotId", bet.getJackpotId());
                            response.put("winner", false);
                            response.put("message", ex.getMessage());
                            return Mono.just(ResponseEntity.ok(response));
                        })
                        .switchIfEmpty(Mono.just(ResponseEntity.ok(Map.of(
                                "betId", bet.getId(),
                                "jackpotId", bet.getJackpotId(),
                                "winner", false,
                                "message", "No reward generated for bet id: " + bet.getId()
                        ))))
                )
                // Throw exception instead of returning empty 404
                .switchIfEmpty(Mono.error(new BetNotFoundException(id)))
                .onErrorResume(ex -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("betId", id);
                    response.put("winner", false);
                    response.put("message", "Unexpected error: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }
}
