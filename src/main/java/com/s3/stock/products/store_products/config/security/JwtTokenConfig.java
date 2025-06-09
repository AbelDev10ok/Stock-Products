package com.s3.stock.products.store_products.config.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class JwtTokenConfig {
    // va a quedar secreta y segura solo en el servidor
    public static final SecretKey SECRET_KEY = Jwts.SIG.HS512.key().build();
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
}
