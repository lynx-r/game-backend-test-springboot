package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Aleksey P. on 25/02/2018.
 */
@RestController()
public class DemoController {

    @GetMapping("/join")
    public void join(@RequestParam("balance") Integer balance) {
        System.out.println(balance);
    }
}
