package com.meli.pushase.api.coupon.application.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends Exception {

    private HttpStatus status;
    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
