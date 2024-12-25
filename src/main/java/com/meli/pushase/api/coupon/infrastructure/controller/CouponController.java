package com.meli.pushase.api.coupon.infrastructure.controller;

import com.meli.pushase.api.coupon.domain.CouponRequest;
import com.meli.pushase.api.coupon.infrastructure.adapter.CouponServiceImpl;
import com.meli.pushase.api.coupon.infrastructure.repository.CouponRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("coupon")
public class CouponController {

    private final CouponServiceImpl couponService;

    @GetMapping()
    public Mono<Map<String,Double>> getCoupon(@Valid @RequestBody CouponRequest couponRequest) {
        return couponService.getMaxAmountItems(couponRequest.getItem_ids(),couponRequest.getAmount());
    }
}
