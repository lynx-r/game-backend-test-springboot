package com.example.demo.dao;

import com.example.demo.domain.RoundMessage;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Aleksey Popryadukhin on 25/02/2018.
 */
public interface MessageRepository extends CrudRepository<RoundMessage, Long> {
}
