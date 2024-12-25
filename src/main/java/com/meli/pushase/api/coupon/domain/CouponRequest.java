package com.meli.pushase.api.coupon.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponRequest {
    private String[] item_ids;
    private double amount;
}
