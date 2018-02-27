package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Aleksey Popryadukhin on 27/02/2018.
 */
@RestController
public class TestService {

    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/test")
    public void test() {
        Session session = sessionRepository.createSession();
        TestDomain testDomain = new TestDomain("a", 1);
        session.setAttribute(session.getId(), testDomain);
        sessionRepository.save(session);

        Session session1 = sessionRepository.getSession(session.getId());
        Object testDomainObj = session1.getAttribute(session1.getId());
        System.out.println("Does testDomainObj from session represent the instance of TestDomain class: " + (testDomainObj instanceof TestDomain));
        TestDomain testDomain1 = (TestDomain) testDomainObj;
        System.out.println(testDomain1);
    }
}
