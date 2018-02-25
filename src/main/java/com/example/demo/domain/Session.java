package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column()
    private Integer balance;
    @JsonIgnore
    @Transient
    private SseEmitter emitter;

    public Session(Integer balance, SseEmitter emitter) {
        this.balance = balance;
        this.emitter = emitter;
    }
}
