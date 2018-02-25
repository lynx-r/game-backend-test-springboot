package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@AllArgsConstructor
@Data
public class RoundMessage {

    private String roundName;
    private int sessionId;
    private int roundResult;
    private int balance;
}
