package com.meli.pushase.api.coupon.infrastructure.repository;

import com.meli.pushase.api.coupon.application.configuration.DynamoDBConfig;
import com.meli.pushase.api.coupon.infrastructure.document.Coupon;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CouponRepository{

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public CouponRepository(DynamoDbAsyncClient dynamoDbAsyncClient) {

        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public Mono<Void> saveProduct(Coupon coupon) {
        Map<String,AttributeValue> item = Map.of(
                "id",AttributeValue.builder().s(coupon.getId()).build(),
                "item_id", AttributeValue.builder().s(coupon.getItem_id()).build(),
                "counter", AttributeValue.builder().n(String.valueOf(coupon.getCounter())).build()
        );
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("CouponTable")
                .item(item)
                .build();
        return Mono.fromFuture(() -> dynamoDbAsyncClient.putItem(putItemRequest)).then();
    }

    public Mono<Void> incrementCounterWithAdd(String productId, int incrementValue) {
        Map<String,AttributeValue> key = Map.of(
                "id", AttributeValue.builder().s(productId).build()
        );
        Map<String,AttributeValue> expressionValues = Map.of(
                ":increment",AttributeValue.builder().n(String.valueOf(incrementValue)).build()
        );
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName("coupons")
                .key(key)
                .updateExpression("ADD counter :increment")
                .expressionAttributeValues(expressionValues)
                .build();
        return Mono.fromFuture(() -> dynamoDbAsyncClient.updateItem(updateRequest)).then();
    }
}
