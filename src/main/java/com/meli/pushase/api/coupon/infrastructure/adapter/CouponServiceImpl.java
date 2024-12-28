package com.meli.pushase.api.coupon.infrastructure.adapter;

import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.application.service.ProductsClient;
import com.meli.pushase.api.coupon.application.service.CouponService;
import com.meli.pushase.api.coupon.domain.CouponResponse;
import com.meli.pushase.api.coupon.domain.ProductClientResponse;
import com.meli.pushase.api.coupon.infrastructure.document.Coupon;
import com.meli.pushase.api.coupon.infrastructure.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    private final ProductsClient productsClient;

    @Override
    public Mono<CouponResponse> getMaxAmountItems(String[] item_ids, double amount) {

        Mono<ProductClientResponse> monoProductsClient = productsClient.getWebProducts(item_ids, amount);
        Mono<Map<String,Double>> monoProducts = monoProductsClient.map(productClientResponse -> productClientResponse.getProducts());
        Mono<CouponResponse> monoCouponResponse = monoProductsClient.map(productClientResponse -> CouponResponse.builder()
                .amount(productClientResponse.getAmount()).build());

        return monoProducts
                .flatMapMany(products -> Flux.fromIterable(products.entrySet()))
                .flatMap(product -> validateAndSaveProduct(product))
                .then(
                        monoProducts.flatMap(products ->
                                monoCouponResponse.map(c ->
                                        CouponResponse.builder()
                                                .amount(c.getAmount())
                                                .item_ids(products.keySet().toArray(new String[0]))
                                                .build()))
                ).onErrorResume(e ->Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())));


    }

    @Override
    public Mono<Map<String, Integer>> getTop5Favorites() {
        Mono<Map<String,Integer>> mapa = couponRepository.getTop5FavoriteCoupons();
        return mapa;
    }

    private Mono<Void> validateAndSaveProduct(Map.Entry<String,Double> product) {
        return couponRepository.isProductNameUnique(product.getKey()).flatMap(isUnique ->{
            if(isUnique){
                return couponRepository.saveCoupon(Coupon.builder()
                        .id("favorite")
                        .item_id(product.getKey())
                        .counter(1)
                        .build());
            }else{
                return couponRepository.incrementCounterWithAdd(product.getKey(), 1);
            }
        });
    }
}
