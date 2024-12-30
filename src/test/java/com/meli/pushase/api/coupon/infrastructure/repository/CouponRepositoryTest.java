package com.meli.pushase.api.coupon.infrastructure.repository;


import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.infrastructure.document.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.properties")
class CouponRepositoryTest {

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @InjectMocks
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        // Este método es ejecutado antes de cada test, asegurando que los mocks se inicialicen
    }

    @Test
    void testSaveCoupon() {
        Coupon coupon = new Coupon("1", "item1", 10);

        // Simulamos la respuesta de DynamoDB para la operación PutItem
        CompletableFuture<PutItemResponse> future = CompletableFuture.completedFuture(PutItemResponse.builder().build());
        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class))).thenReturn(future);

        // Ejecutamos el método
        Mono<Void> result = couponRepository.saveCoupon(coupon);

        // Verificamos el resultado con StepVerifier
        StepVerifier.create(result)
                .verifyComplete();  // Esperamos que el flujo se complete correctamente

        // Verificamos las interacciones con el mock
        verify(dynamoDbAsyncClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void testIncrementCounterWithAdd() {
        // Simulamos la respuesta de DynamoDB para la operación UpdateItem
        CompletableFuture<UpdateItemResponse> future = CompletableFuture.completedFuture(UpdateItemResponse.builder().build());
        when(dynamoDbAsyncClient.updateItem(any(UpdateItemRequest.class))).thenReturn(future);

        // Ejecutamos el método
        Mono<Void> result = couponRepository.incrementCounterWithAdd("item1", 5);

        // Verificamos el resultado con StepVerifier
        StepVerifier.create(result)
                .verifyComplete();  // Esperamos que el flujo se complete correctamente

        // Verificamos las interacciones con el mock
        verify(dynamoDbAsyncClient).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void testIsProductNameUnique() {
        // Simulamos la respuesta de DynamoDB para la operación Query
        QueryResponse queryResponse = QueryResponse.builder().count(0).build();
        CompletableFuture<QueryResponse> future = CompletableFuture.completedFuture(queryResponse);
        when(dynamoDbAsyncClient.query(any(QueryRequest.class))).thenReturn(future);

        // Ejecutamos el método
        Mono<Boolean> result = couponRepository.isProductNameUnique("item1");

        // Verificamos el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNext(true)  // Esperamos que el resultado sea true, ya que no hay elementos con ese item_id
                .verifyComplete();

        // Verificamos las interacciones con el mock
        verify(dynamoDbAsyncClient).query(any(QueryRequest.class));
    }

    @Test
    void testGetTop5FavoriteCoupons() {
        // Simulamos la respuesta de DynamoDB para la operación Query
        Map<String, AttributeValue> item1 = Map.of("item_id", AttributeValue.builder().s("item1").build(),
                "counter", AttributeValue.builder().n("10").build());
        Map<String, AttributeValue> item2 = Map.of("item_id", AttributeValue.builder().s("item2").build(),
                "counter", AttributeValue.builder().n("5").build());

        QueryResponse queryResponse = QueryResponse.builder().items(item1, item2).build();
        CompletableFuture<QueryResponse> future = CompletableFuture.completedFuture(queryResponse);
        when(dynamoDbAsyncClient.query(any(QueryRequest.class))).thenReturn(future);

        // Ejecutamos el método
        Mono<Map<String, Integer>> result = couponRepository.getTop5FavoriteCoupons();

        // Verificamos el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(map -> map.size() == 2 && map.get("item1") == 10 && map.get("item2") == 5)
                .verifyComplete();

        // Verificamos las interacciones con el mock
        verify(dynamoDbAsyncClient).query(any(QueryRequest.class));
    }

    @Test
    void testSaveCoupon_shouldThrowCustomException() {
        Coupon coupon = new Coupon("1", "item1", 10);

        // Simulamos una excepción de DynamoDB
        CompletableFuture<PutItemResponse> future = CompletableFuture.failedFuture(new RuntimeException("DynamoDB error"));
        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class))).thenReturn(future);

        // Ejecutamos el método y verificamos que lance la excepción personalizada
        Mono<Void> result = couponRepository.saveCoupon(coupon);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CustomException &&
                        throwable.getMessage().contains("DynamoDB error"))
                .verify();

        // Verificamos las interacciones con el mock
        verify(dynamoDbAsyncClient).putItem(any(PutItemRequest.class));
    }
}