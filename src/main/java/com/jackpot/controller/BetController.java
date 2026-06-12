package com.jackpot.controller;

import com.jackpot.model.Bet;
import com.jackpot.model.JackpotReward;
import com.jackpot.producer.BetProducer;
import com.jackpot.service.JackpotRewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bets")
public class BetController {
    private final BetProducer betProducer;
    private final JackpotRewardService rewardService;

    public BetController(BetProducer betProducer, JackpotRewardService rewardService) {
        this.betProducer = betProducer;
        this.rewardService = rewardService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> publishBet(@RequestBody Bet bet) {
        return Mono.fromCallable(() -> {
            betProducer.publishBet(bet);
            return ResponseEntity.ok("Bet published");
        });
    }

    @GetMapping("/{betId}/evaluate")
    public Mono<ResponseEntity<JackpotReward>> evaluate(@PathVariable Long betId) {
        return Mono.fromCallable(() ->
                ResponseEntity.of(rewardService.findRewardByBetId(betId))
        );
    }
}


