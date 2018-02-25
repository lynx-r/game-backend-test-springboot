package com.example.demo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class RoundMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "round_name")
    private String roundName;
    @Column(name = "round_result")
    private int roundResult;
    @Column(name = "iteration")
    private int iteration;
    @ManyToOne(targetEntity = Session.class, fetch = FetchType.EAGER)
    private Session session;

    public RoundMessage(String roundName, int roundResult, Session session, int roundIteration) {
        this.roundName = roundName;
        this.roundResult = roundResult;
        this.session = session;
        this.iteration = roundIteration;
    }
}
