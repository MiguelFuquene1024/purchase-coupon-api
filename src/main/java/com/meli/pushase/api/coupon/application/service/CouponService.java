package com.meli.pushase.api.coupon.application.service;

import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.domain.CouponResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

public interface CouponService {
    Mono<CouponResponse> getMaxAmountItems(String[] item_ids, double amount) throws CustomException;
    Mono<Map<String,Integer>> getTop5Favorites();
}
