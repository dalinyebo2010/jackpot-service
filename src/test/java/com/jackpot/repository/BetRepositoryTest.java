package com.jackpot.repository;

import com.jackpot.model.Bet;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class BetRepositoryTest {

    private final BetRepository betRepository = mock(BetRepository.class);

    @Test
    void testSaveAndFindById() {
        Bet bet = new Bet();
        bet.setId(1L);
        bet.setUserId(103L);
        bet.setJackpotId(3L);
        bet.setAmount(BigDecimal.valueOf(140));

        // Mock save to return the bet with an ID
        when(betRepository.save(bet)).thenReturn(Mono.just(bet));
        // Mock findById to return the same bet
        when(betRepository.findById(1L)).thenReturn(Mono.just(bet));

        StepVerifier.create(
                        betRepository.save(bet)
                                .flatMap(saved -> betRepository.findById(saved.getId()))
                )
                .expectNextMatches(found ->
                        found.getUserId().equals(103L) &&
                                found.getJackpotId().equals(3L) &&
                                found.getAmount().compareTo(BigDecimal.valueOf(140)) == 0
                )
                .verifyComplete();

        verify(betRepository).save(bet);
        verify(betRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(betRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(betRepository.findById(999L))
                .verifyComplete(); // should emit nothing

        verify(betRepository).findById(999L);
    }
}
