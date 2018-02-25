package com.example.demo.service;

import com.example.demo.Session;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class MessageService {

    private final SseEmitter emitter = new SseEmitter();

    public SseEmitter getMessages() {
        return emitter;
    }

    void sendSession(Session session) {
        try {
            emitter.send(session, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            emitter.complete();
        }
    }
}