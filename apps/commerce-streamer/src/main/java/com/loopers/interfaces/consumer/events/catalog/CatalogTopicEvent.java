package com.loopers.interfaces.consumer.events.catalog;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "eventType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LikeProductCreated.class, name = "LikeProductCreated"),
        @JsonSubTypes.Type(value = LikeProductDeleted.class, name = "LikeProductDeleted"),
        @JsonSubTypes.Type(value = ProductFound.class, name = "ProductFound")
})
public sealed interface CatalogTopicEvent
        permits LikeProductCreated, LikeProductDeleted, ProductFound {
}