package com.bistroops.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 沒被前面 chain 接住的請求（前台 /、/announcement/** 等）都到這裡
@Configuration
public class DefaultSecurityConfig {

    @Bean
    @Order(99) // 沒有 securityMatcher = 匹配所有請求，所以一定要排最後
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
