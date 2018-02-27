package com.example.demo.service;

import com.example.demo.config.AppProperties;
import com.example.demo.dao.MessageRepository;
import com.example.demo.domain.RoundMessage;
import com.example.demo.domain.RoundSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
@Service
public class RoundService {

    /**
     * Link to the latest thread
     */
    private RoundThread currentRound;

    private Map<String, SseEmitter> sessionIdSseEmitter = new HashMap<>();
    private SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final AppProperties appProperties;
    private final AutowireCapableBeanFactory factory;

    @Autowired
    public RoundService(SessionRepository sessionRepository, MessageRepository messageRepository,
                        AppProperties appProperties, AutowireCapableBeanFactory factory) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.appProperties = appProperties;
        this.factory = factory;
    }

    @PostConstruct
    private void init() {
        startRoundThread();
    }

    private void startRoundThread() {
        // Initialize rounds-threads
        new Thread(() -> {
            int i = 0;
            while (i < appProperties.getMaxRounds() || appProperties.getMaxRounds() == 0) {
                currentRound = factory.createBean(RoundThread.class);
                currentRound.setName("Round-" + i);
                currentRound.start();
                try {
                    TimeUnit.SECONDS.sleep(appProperties.getIntervalBetweenRounds());
                } catch (InterruptedException e) {
                    System.out.println("New round");
                }
                i++;
            }
        }).start();
    }

    public SseEmitter joinWithBalance(int balance) {
        Session session = sessionRepository.createSession();
        sessionIdSseEmitter.put(session.getId(), new SseEmitter());

        RoundSession roundSession = new RoundSession(session.getId(), balance - 1);
        session.setAttribute(session.getId(), roundSession);
        sessionRepository.save(session);
        return currentRound.addSession(roundSession);
    }

    public SseEmitter getSseEmitterBySessionId(String sessionId) {
        return sessionIdSseEmitter.get(sessionId);
    }

    public RoundSession saveSession(RoundSession roundSession) {
        Session session = sessionRepository.getSession(roundSession.getSessionId());
        session.setAttribute(roundSession.getSessionId(), roundSession);
        sessionRepository.save(session);
        return roundSession;
    }

    public RoundMessage createMessage(String roundName, String sessionId, int roundResult, int roundIteration) {
        Session session = sessionRepository.getSession(sessionId);
        Object obj = session.getAttribute(sessionId);
        RoundSession roundSession = new RoundSession((RoundSession)obj);
        System.out.println(roundSession.getClass().getSimpleName());
        System.out.println(roundSession);
        RoundMessage message = new RoundMessage(roundName, roundResult, null, roundIteration);
        return messageRepository.save(message);
    }
}
