package com.meli.pushase.api.coupon.application.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String message;

}
