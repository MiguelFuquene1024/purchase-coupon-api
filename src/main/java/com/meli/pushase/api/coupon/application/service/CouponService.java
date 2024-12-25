package com.meli.pushase.api.coupon.application.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CouponService {
    Mono<Map<String,Double>> getMaxAmountItems(String[] item_ids, double amount);
}
