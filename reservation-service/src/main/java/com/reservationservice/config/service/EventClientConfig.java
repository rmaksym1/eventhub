package com.reservationservice.config.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EventClientConfig {

    @Bean
    public RestClient eventRestClient() {
        return RestClient.create(
                "http://event-service:8081"
        );
    }
}