package com.s3.stock.products.store_products.config.security.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleGrentesAuthorityJson {
    @JsonCreator
    public SimpleGrentesAuthorityJson(@JsonProperty("authority") String role) {
        
    }

}
