package com.meli.pushase.api.coupon.infrastructure.document;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
@Data
@DynamoDbBean
public class Coupon {

    private String id;
    private String item_id;
    private int counter;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

}
