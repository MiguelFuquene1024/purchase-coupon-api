package com.meli.pushase.api.coupon.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductClientResponse {
    private Map<String,Double> products;
    private Double amount;
}
