package com.example.demo.service;

import com.example.demo.config.Config;
import com.example.demo.dao.MessageRepository;
import com.example.demo.dao.SessionRepository;
import com.example.demo.domain.RoundMessage;
import com.example.demo.domain.Session;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
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

    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    @Inject
    public RoundService(SessionRepository sessionRepository, MessageRepository messageRepository,
                        Config config, AutowireCapableBeanFactory factory) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        // Initialize rounds-threads
        new Thread(() -> {
            int i = 0;
            while (i < config.getMaxRounds() || config.getMaxRounds() == 0) {
                currentRound = factory.createBean(RoundThread.class);
                currentRound.setName("Round-" + i);
                currentRound.start();
                try {
                    TimeUnit.SECONDS.sleep(config.getIntervalBetweenRounds());
                } catch (InterruptedException e) {
                    System.out.println("New round");
                }
                i++;
            }
        }).start();
    }

    public SseEmitter joinWithBalance(int balance) {
        Session session = new Session(balance - 1, new SseEmitter());
        session = sessionRepository.save(session);
        return currentRound.addSession(session);
    }

    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    public RoundMessage saveMessage(RoundMessage message) {
        return messageRepository.save(message);
    }

    public RoundMessage createMessage(String roundName, Long sessionId, int roundResult, int roundIteration) {
        Session one = sessionRepository.findOne(sessionId);
        RoundMessage message = new RoundMessage(roundName, roundResult, one, roundIteration);
        return messageRepository.save(message);
    }
}
