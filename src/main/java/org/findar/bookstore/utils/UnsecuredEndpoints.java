package org.findar.bookstore.utils;

public class UnsecuredEndpoints {

    public static final String[] UNSECURED_ENDPOINT = {
            "/swagger",
            "/swagger-ui/**",
            "/v3/**",
            "/h2-console/**",
            "/h2/**",
            "/api/v1/user/login",
            "/api/v1/user/register",
    };
}
