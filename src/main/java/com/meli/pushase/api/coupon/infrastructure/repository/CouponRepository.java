package com.meli.pushase.api.coupon.infrastructure.repository;

import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.infrastructure.document.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CouponRepository{

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public Mono<Void> saveCoupon(Coupon coupon) {
        Map<String,AttributeValue> item = Map.of(
                "id",AttributeValue.builder().s(coupon.getId()).build(),
                "item_id", AttributeValue.builder().s(coupon.getItem_id()).build(),
                "counter", AttributeValue.builder().n(String.valueOf(coupon.getCounter())).build()
        );
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("CouponTable")
                .item(item)
                .build();
        return Mono.fromFuture(() -> dynamoDbAsyncClient.putItem(putItemRequest)).then()
                .onErrorResume(e->Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())));
    }

    public Mono<Void> incrementCounterWithAdd(String item_id, int incrementValue) {
        Map<String,AttributeValue> key = Map.of(
                "id", AttributeValue.builder().s("favorite").build(),
                "item_id", AttributeValue.builder().s(item_id).build());
        Map<String,AttributeValue> expressionValues = Map.of(
                ":increment",AttributeValue.builder().n(String.valueOf(incrementValue)).build()
        );
        Map<String, String> expressionNames = Map.of(
                "#counter", "counter"
        );
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName("CouponTable")
                .key(key)
                .expressionAttributeNames(expressionNames)
                .updateExpression("ADD #counter :increment")
                .expressionAttributeValues(expressionValues)
                .build();
        return Mono.fromFuture(() -> dynamoDbAsyncClient.updateItem(updateRequest)).then()
                .onErrorResume(e->Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())));
    }
    public Mono<Boolean> isProductNameUnique(String item_id) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("CouponTable")
                .indexName("item_id-index")
                .keyConditionExpression("item_id = :nameValue")
                .expressionAttributeValues(Map.of(
                        ":nameValue", AttributeValue.builder().s(item_id).build()
                ))
                .build();
        Mono<Boolean> flag = Mono.fromFuture(() -> dynamoDbAsyncClient.query(queryRequest))
                .map(queryResponse -> queryResponse.count() == 0);
        return flag;
    }
    public Mono<Map<String,Integer>> getTop5FavoriteCoupons() {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("CouponTable")
                .indexName("id-counter-index")
                .keyConditionExpression("id = :partitionKey")
                .expressionAttributeValues(Map.of(
                        ":partitionKey", AttributeValue.builder().s("favorite").build()
                ))
                .scanIndexForward(false)
                .limit(5)
                .build();


        return Mono.fromFuture(() -> dynamoDbAsyncClient.query(queryRequest))
                .map(QueryResponse::items)
                .onErrorResume(e->Mono.error(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())))
                .map(items -> items.stream()
                        .collect(Collectors.toMap(item -> item.get("item_id").s(),
                                item -> Integer.parseInt(item.get("counter").n()),
                                (oldValue,newValue) -> oldValue,
                                LinkedHashMap::new
                        )));
    }
    private Coupon mapToCoupon(Map<String, AttributeValue> item) {
        return new Coupon(
                item.get("id").s(),
                item.get("item_id").s(),
                Integer.parseInt(item.get("counter").n())
        );
    }

}
