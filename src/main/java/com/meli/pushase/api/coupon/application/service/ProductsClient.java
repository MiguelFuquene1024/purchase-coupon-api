package com.meli.pushase.api.coupon.application.service;


import com.meli.pushase.api.coupon.domain.CouponResponse;
import com.meli.pushase.api.coupon.domain.Product;
import com.meli.pushase.api.coupon.domain.ProductClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsClient {

    private final WebClient webClient;

    public Mono<ProductClientResponse> getWebProducts(String[] productIds,Double amount){
        Mono<Map<String, Double>> products = Flux.fromArray(productIds)
                .flatMap(this::fetchProduct)
                .sort((p1,p2) -> Double.compare(p1.getPrice(), p2.getPrice()))
                .collect(Collectors.toMap(
                        Product::getId,
                        Product::getPrice,
                        (oldValue,newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return getMaxProductsWithinBudget(products, amount);
    }
    public Mono<ProductClientResponse> getMaxProductsWithinBudget(Mono<Map<String, Double>> productsMono, Double totalAmount) {
        return productsMono
                .flatMap(products -> {
                    Map<String, Double> selectedProducts = new LinkedHashMap<>();
                    double amount = 0;

                    for (Map.Entry<String, Double> entry : products.entrySet()) {
                        String productId = entry.getKey();
                        Double productPrice = entry.getValue();
                        amount += productPrice;
                        if (amount <= totalAmount) {
                            selectedProducts.put(productId, productPrice);
                        }else{
                            amount-=productPrice;
                            break;
                        }
                    }
                    return Mono.just(ProductClientResponse.builder()
                            .products(selectedProducts)
                            .amount(amount).build());
                });
    }

    private Mono<Product> fetchProduct(String productId){
        return webClient.get().uri("/items/{id}",productId)
                .retrieve()
                .bodyToMono(Product.class);
    }


}
