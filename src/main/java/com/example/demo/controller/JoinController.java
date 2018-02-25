package com.example.demo.controller;

import com.example.demo.service.RoundRunnable;
import com.example.demo.config.Config;
import com.example.demo.service.DemoService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
@RestController
public class JoinController {

    private DemoService demoService;
    private RoundRunnable currentRound;

    @Inject
    public JoinController(DemoService demoService, Config config, AutowireCapableBeanFactory factory) {
        this.demoService = demoService;
        // Initialize rounds-threads
        new Thread(() -> {
            int i = 0;
            while (i < config.getMaxRounds() || config.getMaxRounds() == 0) {
                currentRound = factory.createBean(RoundRunnable.class);
                Thread thread = new Thread(currentRound);
                thread.setName("Round-" + i);
                thread.start();
                try {
                    TimeUnit.SECONDS.sleep(config.getIntervalBetweenRounds());
                } catch (InterruptedException e) {
                    System.out.println("New round");
                }
                i++;
            }
        }).start();
    }

    @GetMapping("/join")
    public SseEmitter join(@RequestParam("balance") Integer balance) {
        return demoService.joinWithBalance(currentRound, balance);
    }
}
