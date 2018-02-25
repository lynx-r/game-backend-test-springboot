package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@AllArgsConstructor
@Data
public class Session {
    private int id;
    private int balance;
}
