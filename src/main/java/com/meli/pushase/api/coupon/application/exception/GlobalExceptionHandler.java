package com.meli.pushase.api.coupon.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Mono<CustomException>> handleResponseStatusException(CustomException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Mono.just(new CustomException(ex.getStatus(), ex.getMessage())));
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Mono<String>> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Mono.just(ex.getMessage()));
    }
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Mono<ErrorResponse>> handleGenericException(ServerWebInputException ex) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Mono.just(new ErrorResponse(HttpStatus.BAD_REQUEST,"Error al leer la entrada HTTP")));

    }
    // Manejo de excepciones genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<ErrorResponse>> handleGenericException(Exception ex) {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Mono.just(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage())));

    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<Mono<String>> handleMethodNotAllowed(MethodNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Mono.just("Método no permitido."));

    }
}
