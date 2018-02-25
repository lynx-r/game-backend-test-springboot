package com.example.demo;

import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
public class Round implements Runnable {

    private final Config config;
    private int roundCounter = 0;

    private Random random = new Random();

    @Getter
    private ThreadLocal<Set<Session>> sessionsThreadLocal;
    @Getter
    private ThreadLocal<SseEmitter> emitterThreadLocal;

    private Set<Session> sessions = new HashSet<>();
    private SseEmitter emitter = new SseEmitter();

    public Round(Config config) {
        sessionsThreadLocal = ThreadLocal.withInitial(() -> sessions);
        emitterThreadLocal = ThreadLocal.withInitial(() -> emitter);
        this.config = config;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            int roundResult = random.nextInt(config.getMaxPossibleResults());
            System.out.println(Thread.currentThread().getName() + ". Random number " + roundResult);
            if (roundResult == config.getWinResult()) {
                sessionsThreadLocal.get().forEach(session -> {
                    System.out.println("Win " + Thread.currentThread().getName() + " Session " + session.getId() + ", increased balance to " + session.getBalance());
                    session.setBalance(session.getBalance() + config.getIncWinBalance());
                });
            }
            sessionsThreadLocal.get().forEach(session -> {
                try {
                    RoundMessage message = new RoundMessage(Thread.currentThread().getName(), roundResult, session.getBalance());
                    emitter.send(message, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.complete();
                }
            });
            try {
                TimeUnit.SECONDS.sleep(config.getNextRoundSeconds());
            } catch (InterruptedException e) {
                System.out.println("Round #" + Thread.currentThread().getName() + ". Next iteration " + roundCounter);
            }
            roundCounter++;
        }
    }

    public void addSession(Session session) {
        sessionsThreadLocal.get().add(session);
    }
}
