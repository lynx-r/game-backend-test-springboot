package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Aleksey Popryadukhin on 27/02/2018.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TestDomain implements Serializable {
    private static final long serialVersionUID = 2526867211140708856L;

    private String s;
    private int i;
}
