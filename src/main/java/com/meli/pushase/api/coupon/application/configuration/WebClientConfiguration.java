package com.meli.pushase.api.coupon.application.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfiguration {

    @Value("${maxConnections}")
    private int maxConnections;

    @Value("${acquireTimeout}")
    private long acquireTimeout;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create(ConnectionProvider
                .builder("connectionProvider")
                .maxConnections(maxConnections)
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofMillis(acquireTimeout))
                .build());
        String productURL = "https://api.mercadolibre.com";
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(productURL).build();
    }
}
