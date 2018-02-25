package com.example.demo.service;

import com.example.demo.dao.MessageRepository;
import com.example.demo.dao.SessionRepository;
import com.example.demo.domain.RoundMessage;
import com.example.demo.domain.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@Service
public class DemoService {

    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    @Inject
    public DemoService(SessionRepository sessionRepository, MessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    public SseEmitter joinWithBalance(RoundRunnable round, int balance) {
        Session session = new Session(balance - 1, new SseEmitter());
        session = sessionRepository.save(session);
        return round.addSession(session);
    }

    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    public RoundMessage saveMessage(RoundMessage message) {
        return messageRepository.save(message);
    }

    public RoundMessage createMessage(String roundName, Long sessionId, int roundResult) {
        Session one = sessionRepository.findOne(sessionId);
        RoundMessage message = new RoundMessage(roundName, roundResult, one);
        return messageRepository.save(message);
    }
}
