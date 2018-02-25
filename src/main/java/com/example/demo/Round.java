package com.example.demo;

import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
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

    private Set<Session> sessions = new HashSet<>();

    public Round(Config config) {
        sessionsThreadLocal = ThreadLocal.withInitial(() -> sessions);
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
            System.out.println("Session count " + sessionsThreadLocal.get().size());
            sessionsThreadLocal.get().forEach(session -> {
                RoundMessage message = getRoundMessage(roundResult, session);
                try {
                    session.getEmitter().send(message, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    e.printStackTrace();
                    session.getEmitter().complete();
                }
                sessionsThreadLocal.get().stream().filter(s -> s.getId() != session.getId()).forEach(session1 -> {
                    RoundMessage innerMessage = getRoundMessage(roundResult, session1);
                    try {
                        System.out.println(innerMessage);
                        session.getEmitter().send(innerMessage, MediaType.APPLICATION_JSON);
                    } catch (IOException e) {
                        e.printStackTrace();
                        session.getEmitter().complete();
                    }
                });
            });
            try {
                TimeUnit.SECONDS.sleep(config.getNextRoundSeconds());
            } catch (InterruptedException e) {
                System.out.println("Round #" + Thread.currentThread().getName() + ". Next iteration " + roundCounter);
            }
            roundCounter++;
        }
    }

    private RoundMessage getRoundMessage(int roundResult, Session session) {
        return new RoundMessage(Thread.currentThread().getName(),
                session.getId(), roundResult, session.getBalance());
    }

    public SseEmitter addSession(int clientId, int balance) {
        Optional<Session> sessionOpt = sessionsThreadLocal.get().stream().filter(s -> s.getId() == clientId).findFirst();
        if (sessionOpt.isPresent()) {
            return sessionOpt.get().getEmitter();
        }
        Session session = new Session(clientId, balance, new SseEmitter(Long.MAX_VALUE));
        sessionsThreadLocal.get().add(session);
        return session.getEmitter();
    }
}
