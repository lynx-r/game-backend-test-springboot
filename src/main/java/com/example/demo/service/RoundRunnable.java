package com.example.demo.service;

import com.example.demo.config.Config;
import com.example.demo.domain.RoundMessage;
import com.example.demo.domain.Session;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@Component
public class RoundRunnable implements Runnable {

    private final Config config;
    private final DemoService demoService;
    private int roundCounter = 0;
    private Random random = new Random();

    /**
     * Sessions for this thread
     */
    private Set<Session> sessions = new HashSet<>();
    @Getter
    private ThreadLocal<Set<Session>> sessionsThreadLocal;

    @Inject
    RoundRunnable(DemoService demoService, Config config) {
        sessionsThreadLocal = ThreadLocal.withInitial(() -> sessions);
        this.config = config;
        this.demoService = demoService;
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
                    demoService.saveSession(session);
                });
            }
            System.out.println("Session count " + sessionsThreadLocal.get().size());
            sessionsThreadLocal.get().forEach(session -> {
                RoundMessage message = createRoundMessage(roundResult, session.getId());
                sendMessage(session.getEmitter(), message);
                sessionsThreadLocal.get().stream().filter(s -> !s.getId().equals(session.getId())).forEach(session1 -> {
                    RoundMessage innerMessage = createRoundMessage(roundResult, session.getId());
                    sendMessage(session1.getEmitter(), innerMessage);
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

    /**
     * Add session to the local thread and create an emitter which will send messages to clients
     * @param session
     * @return
     */
    public SseEmitter addSession(Session session) {
        Optional<Session> sessionOpt = sessionsThreadLocal.get().stream().filter(s -> s.getId().equals(session.getId())).findFirst();
        if (sessionOpt.isPresent()) {
            return sessionOpt.get().getEmitter();
        }
        sessionsThreadLocal.get().add(session);
        return session.getEmitter();
    }

    /**
     * Send a message over SSE
     * @param emitter
     * @param message
     */
    private void sendMessage(SseEmitter emitter, RoundMessage message) {
        try {
            emitter.send(message, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            e.printStackTrace();
            emitter.complete();
        }
    }

    /**
     * Form a message to be sent
     * @param roundResult
     * @param sessionId
     * @return
     */
    private RoundMessage createRoundMessage(int roundResult, Long sessionId) {
        return demoService.createMessage(Thread.currentThread().getName(), sessionId, roundResult);
    }
}
