package com.meli.pushase.api.coupon.application.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient() {
        String productURL = "https://api.mercadolibre.com";
        return WebClient.builder().baseUrl(productURL).build();
    }
}
