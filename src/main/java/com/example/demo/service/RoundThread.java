package com.example.demo.service;

import com.example.demo.config.AppProperties;
import com.example.demo.domain.RoundMessage;
import com.example.demo.domain.RoundSession;
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
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
@Component
public class RoundThread extends Thread {

    private final AppProperties appProperties;
    private final RoundService roundService;
    private Integer iteration = 0;
    private ThreadLocal<Integer> roundCounter = ThreadLocal.withInitial(() -> iteration);
    private Random random = new Random();

    /**
     * Sessions for this thread
     */
    private Set<RoundSession> sessions = new HashSet<>();
    @Getter
    private ThreadLocal<Set<RoundSession>> sessionsThreadLocal;

    @Inject
    RoundThread(RoundService roundService, AppProperties appProperties) {
        sessionsThreadLocal = ThreadLocal.withInitial(() -> sessions);
        this.appProperties = appProperties;
        this.roundService = roundService;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            int roundResult = random.nextInt(appProperties.getMaxPossibleResults());
            System.out.println(Thread.currentThread().getName() + ". Random number " + roundResult);
            if (roundResult == appProperties.getWinResult()) {
                sessionsThreadLocal.get().forEach(session -> {
                    System.out.println("Win " + Thread.currentThread().getName() + " RoundSession " + session.getSessionId() + ", increased balance to " + session.getBalance());
                    session.setBalance(session.getBalance() + appProperties.getIncWinBalance());
                    roundService.saveSession(session);
                });
            }
            System.out.println("RoundSession count " + sessionsThreadLocal.get().size());
            // double loop for sessions to send a message to each session in the current thread
            sessionsThreadLocal.get().forEach(session -> {
                RoundMessage message = createRoundMessage(roundResult, session.getSessionId(), roundCounter.get());
                sendMessage(roundService.getSseEmitterBySessionId(session.getSessionId()), message);
                sessionsThreadLocal.get()
                        .stream()
                        .filter(s -> !s.getSessionId().equals(session.getSessionId()))
                        .forEach(session1 -> {
                            RoundMessage innerMessage = createRoundMessage(roundResult, session.getSessionId(), roundCounter.get());
                            sendMessage(roundService.getSseEmitterBySessionId(session1.getSessionId()), innerMessage);
                        });
            });
            try {
                TimeUnit.SECONDS.sleep(appProperties.getNextRoundSeconds());
            } catch (InterruptedException e) {
                System.out.println("Round #" + Thread.currentThread().getName() + ". Next iteration " + roundCounter);
            }
            roundCounter.set(roundCounter.get() + 1);
        }
    }

    /**
     * Add session to the local thread and create an emitter which will send messages to clients
     *
     * @param session
     * @return
     */
    public SseEmitter addSession(RoundSession session) {
        Optional<RoundSession> sessionOpt = sessionsThreadLocal.get()
                .stream()
                .filter(s -> s.getSessionId().equals(session.getSessionId()))
                .findFirst();
        if (sessionOpt.isPresent()) {
            return roundService.getSseEmitterBySessionId(sessionOpt.get().getSessionId());
        }
        sessionsThreadLocal.get().add(session);
        return roundService.getSseEmitterBySessionId(session.getSessionId());
    }

    /**
     * Send a message over SSE
     *
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
     *
     * @param roundResult
     * @param sessionId
     * @param roundIteration
     * @return
     */
    private RoundMessage createRoundMessage(int roundResult, String sessionId, int roundIteration) {
        return roundService.createMessage(Thread.currentThread().getName(), sessionId, roundResult, roundIteration);
    }
}
