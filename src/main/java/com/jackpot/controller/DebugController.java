package com.jackpot.controller;

import com.jackpot.model.Jackpot;
import com.jackpot.repository.JackpotRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final JackpotRepository jackpotRepository;

    public DebugController(JackpotRepository jackpotRepository) {
        this.jackpotRepository = jackpotRepository;
    }

    @GetMapping("/jackpots")
    public List<Jackpot> getJackpots() {
        return jackpotRepository.findAll();
    }
}

