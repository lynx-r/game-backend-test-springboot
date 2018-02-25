package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@RestController
public class JoinController {

    private Round currentRound;

    @Inject
    public JoinController(Config config) {
        new Thread(() -> {
            for (int i = 0; i < config.getMaxRounds(); i++) {
                currentRound = new Round(config);
                Thread thread = new Thread(currentRound);
                thread.setName("Round-" + i);
                thread.start();
                try {
                    TimeUnit.SECONDS.sleep(config.getIntervalBetweenRounds());
                } catch (InterruptedException e) {
                    System.out.println("New round");
                }
            }
        }).start();
    }

    @GetMapping("/join")
    public SseEmitter join(@RequestParam("client_id") Integer clientId, @RequestParam("balance") Integer balance) {
        Session session = new Session(clientId, balance - 1);
        currentRound.addSession(session);
        return currentRound.getEmitterThreadLocal().get();
    }
}