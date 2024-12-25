package com.meli.pushase.api.coupon.infrastructure.adapter;

import com.meli.pushase.api.coupon.application.service.Calculator;
import com.meli.pushase.api.coupon.application.service.CouponService;
import com.meli.pushase.api.coupon.infrastructure.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private CouponRepository couponRepository;

    private final Calculator calculator;

    @Override
    public Mono<Map<String,Double>> getMaxAmountItems(String[] item_ids, double amount) {
        Mono<Map<String,Double>> products = calculator.getWebProducts(item_ids);

        return products;//products.map

    }
}
