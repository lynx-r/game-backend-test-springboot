package com.example.demo.controller;

import com.example.demo.service.RoundService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
@RestController
public class JoinController {

    private RoundService roundService;

    @Inject
    public JoinController(RoundService roundService) {
        this.roundService = roundService;
    }

    /**
     * Join to current round with balance
     * @param balance initial balance of a client
     * @return SSE channel which notifies a client by sending @RoundMessage
     */
    @GetMapping("/join")
    public SseEmitter join(@NotNull @RequestParam("balance") Integer balance) {
        return roundService.joinWithBalance(balance);
    }
}
