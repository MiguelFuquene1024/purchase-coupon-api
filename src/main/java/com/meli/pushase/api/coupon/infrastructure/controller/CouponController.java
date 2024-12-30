package com.meli.pushase.api.coupon.infrastructure.controller;

import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.domain.CouponRequest;
import com.meli.pushase.api.coupon.domain.CouponResponse;
import com.meli.pushase.api.coupon.infrastructure.adapter.CouponServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("coupon")
public class CouponController {

    private final CouponServiceImpl couponService;

    @PostMapping()
    public Mono<CouponResponse> getCoupon(@Valid @RequestBody CouponRequest couponRequest) {
        return couponService.getMaxAmountItems(couponRequest.getItem_ids(),couponRequest.getAmount());
    }
    @GetMapping("/top5")
    public Mono<Map<String,Integer>> getTop5Coupon() {
        return couponService.getTop5Favorites();
    }
    @GetMapping()
    public Mono<ResponseEntity<?>> getHealthCheck() {
        return Mono.just(ResponseEntity.ok("Bienvenidos"));
    }
}
