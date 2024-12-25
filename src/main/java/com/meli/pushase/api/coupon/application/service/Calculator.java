package com.meli.pushase.api.coupon.application.service;


import com.meli.pushase.api.coupon.domain.Product;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Calculator {

    private final WebClient webClient;

    public Mono<Map<String,Double>> getWebProducts(String[] productIds){
        return Flux.fromArray(productIds)
                .flatMap(this::fetchProduct)
                .sort((p1,p2) -> Double.compare(p1.getPrice(), p2.getPrice()))
                .collect(Collectors.toMap(
                        Product::getId,
                        Product::getPrice,
                        (oldValue,newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private Mono<Product> fetchProduct(String productId){
        return webClient.get().uri("/items/{id}",productId)
                .retrieve()
                .bodyToMono(Product.class);
    }


}
