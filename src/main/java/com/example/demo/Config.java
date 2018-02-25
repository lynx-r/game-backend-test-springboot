package com.example.demo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@Data
@Configuration
public class Config {

    @Value(value = "${interval_between_rounds}")
    private Integer intervalBetweenRounds;

    @Value(value = "${max_possible_results}")
    private Integer maxPossibleResults;

    @Value(value = "${win_result}")
    private Integer winResult;

    @Value(value = "${next_round_seconds}")
    private Integer nextRoundSeconds;

    @Value(value = "${inc_win_balance}")
    private Integer incWinBalance;

    @Value(value = "${max_rounds}")
    private Integer maxRounds;
}
