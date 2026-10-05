package com.bistroops.security.staticresources;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 靜態資源放行
@Configuration
public class StaticResourceSecurityConfig {

	@Bean
	@Order(0) // 注意：@Order 要加在 @Bean 方法上，加在 class 上對 SecurityFilterChain 無效
	public SecurityFilterChain staticResourceFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/common/**", "/entry/**", "/front/**", "/staff/*/css/**", "/staff/*/js/**", "/webjars/**", "/favicon.ico")
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}
