package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcOperationsSessionRepository;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AppConfig {

    @Bean
    SessionRepository sessionFactoryBean(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
        JdbcOperationsSessionRepository sessionRepository = new JdbcOperationsSessionRepository(jdbcTemplate, transactionManager);
        GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(Object.class, byte[].class,
                new SerializingConverter());
        conversionService.addConverter(byte[].class, Object.class,
                new DeserializingConverter(Thread.currentThread().getContextClassLoader()));
        sessionRepository.setConversionService(conversionService);
        return sessionRepository;
    }
}