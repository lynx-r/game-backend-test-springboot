package com.example.demo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Aleksey P. on 25/02/2018.
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
    @ManyToOne(targetEntity = Session.class, fetch = FetchType.EAGER)
    private Session session;

    public RoundMessage(String roundName, int roundResult, Session session) {
        this.roundName = roundName;
        this.roundResult = roundResult;
        this.session = session;
    }
}
