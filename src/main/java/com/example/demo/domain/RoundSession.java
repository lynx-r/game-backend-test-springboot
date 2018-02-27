package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoundSession implements Serializable {

    private static final long serialVersionUID = 7832102299757404835L;
    private String sessionId;
    private int balance;

    public RoundSession(RoundSession roundSession) {
        System.out.println(roundSession);
    }
}
