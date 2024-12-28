package com.meli.pushase.api.coupon.infrastructure.repository;


import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.infrastructure.document.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CouponRepositoryTest {

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @InjectMocks
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testSaveCoupon() {
        // Crear un objeto Coupon
        Coupon coupon = new Coupon("1", "item1", 10);

        // Simular la respuesta de DynamoDB
        CompletableFuture<PutItemResponse> future = CompletableFuture.completedFuture(PutItemResponse.builder().build());
        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class))).thenReturn(future);

        // Ejecutar el método
        Mono<Void> result = couponRepository.saveCoupon(coupon);

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .verifyComplete();  // Verifica que el flujo se complete correctamente

        // Verificar interacciones con el mock
        verify(dynamoDbAsyncClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void testIncrementCounterWithAdd() {
        // Simular la respuesta de DynamoDB
        CompletableFuture<UpdateItemResponse> future = CompletableFuture.completedFuture(UpdateItemResponse.builder().build());
        when(dynamoDbAsyncClient.updateItem(any(UpdateItemRequest.class))).thenReturn(future);

        // Ejecutar el método
        Mono<Void> result = couponRepository.incrementCounterWithAdd("item1", 5);

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .verifyComplete();  // Verifica que el flujo se complete correctamente

        // Verificar interacciones con el mock
        verify(dynamoDbAsyncClient).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void testIsProductNameUnique() {
        // Simular la respuesta de DynamoDB
        QueryResponse queryResponse = QueryResponse.builder().count(0).build();
        CompletableFuture<QueryResponse> future = CompletableFuture.completedFuture(queryResponse);
        when(dynamoDbAsyncClient.query(any(QueryRequest.class))).thenReturn(future);

        // Ejecutar el método
        Mono<Boolean> result = couponRepository.isProductNameUnique("item1");

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNext(true)  // Esperamos que el resultado sea true, ya que no hay elementos con ese item_id
                .verifyComplete();

        // Verificar interacciones con el mock
        verify(dynamoDbAsyncClient).query(any(QueryRequest.class));
    }

    @Test
    void testGetTop5FavoriteCoupons() {
        // Simular la respuesta de DynamoDB
        Map<String, AttributeValue> item1 = Map.of("item_id", AttributeValue.builder().s("item1").build(),
                "counter", AttributeValue.builder().n("10").build());
        Map<String, AttributeValue> item2 = Map.of("item_id", AttributeValue.builder().s("item2").build(),
                "counter", AttributeValue.builder().n("5").build());

        QueryResponse queryResponse = QueryResponse.builder().items(item1, item2).build();
        CompletableFuture<QueryResponse> future = CompletableFuture.completedFuture(queryResponse);
        when(dynamoDbAsyncClient.query(any(QueryRequest.class))).thenReturn(future);

        // Ejecutar el método
        Mono<Map<String, Integer>> result = couponRepository.getTop5FavoriteCoupons();

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(map -> map.size() == 2 && map.get("item1") == 10 && map.get("item2") == 5)
                .verifyComplete();

        // Verificar interacciones con el mock
        verify(dynamoDbAsyncClient).query(any(QueryRequest.class));
    }

    @Test
    void testSaveCoupon_shouldThrowCustomException() {
        // Crear un objeto Coupon
        Coupon coupon = new Coupon("1", "item1", 10);

        // Simular una excepción de DynamoDB
        CompletableFuture<PutItemResponse> future = CompletableFuture.failedFuture(new RuntimeException("DynamoDB error"));
        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class))).thenReturn(future);

        // Ejecutar el método y verificar que lance la excepción personalizada
        Mono<Void> result = couponRepository.saveCoupon(coupon);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CustomException &&
                        throwable.getMessage().contains("DynamoDB error"))
                .verify();

        // Verificar interacciones con el mock
        verify(dynamoDbAsyncClient).putItem(any(PutItemRequest.class));
    }
}