package com.jackpot.controller;

import com.jackpot.model.Bet;
import com.jackpot.model.JackpotReward;
import com.jackpot.producer.BetProducer;
import com.jackpot.repository.BetRepository;
import com.jackpot.service.JackpotRewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/bets")
public class BetController {

    private final BetProducer betProducer;
    private final JackpotRewardService rewardService;
    private final BetRepository betRepository;

    public BetController(BetProducer betProducer,
                         JackpotRewardService rewardService,
                         BetRepository betRepository) {
        this.betProducer = betProducer;
        this.rewardService = rewardService;
        this.betRepository = betRepository;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> publishBet(@RequestBody Bet bet) {
        // Kafka producer is blocking → wrap in boundedElastic
        return Mono.fromCallable(() -> {
            betProducer.publishBet(bet);
            return ResponseEntity.ok("Bet published");
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{betId}/evaluate")
    public Mono<ResponseEntity<JackpotReward>> evaluate(@PathVariable Long betId) {
        return betRepository.findById(betId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Bet not found")))
                .flatMap(rewardService::evaluateReward)
                .map(ResponseEntity::ok)
                // Always return 200 OK, even if no reward was generated
                .defaultIfEmpty(ResponseEntity.ok().build());
    }
}
