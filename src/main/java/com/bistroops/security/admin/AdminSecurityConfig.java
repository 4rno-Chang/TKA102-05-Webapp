package com.bistroops.security.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

// 後台 /admin/** 的安全設定
@Configuration
public class AdminSecurityConfig {

	@Value("${bistroops.admin.security-enabled:true}") // 沒設定時預設 true（比較安全）
	private boolean securityEnabled;
	
	private final AdminUserDetailsService adminUserDetailsService;

	public AdminSecurityConfig(AdminUserDetailsService adminUserDetailsService) {
		this.adminUserDetailsService = adminUserDetailsService;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/admin/**");

		if (securityEnabled) {
			http.authorizeHttpRequests(auth -> auth
					.requestMatchers("/admin/login")
					.permitAll()     // 登入頁一定要放行，否則會無限重導
					.requestMatchers("/admin/announcement/**")
					.hasAuthority("PERM_5") // 5 = 公告設定
					.anyRequest()
					.authenticated());     // 其他後台頁面：登入後才能進
		} else {
			http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 開發模式：全部放行
		}

		http.userDetailsService(adminUserDetailsService)
				.formLogin(form -> form.loginPage("/admin/login") // GET：顯示登入頁（AdminIndexController）
				.loginProcessingUrl("/admin/login") // POST：由 Security 處理帳密比對，不用寫 Controller
				.usernameParameter("empNo") // 對應 login.html 的 name="empNo"
				.passwordParameter("empPassword") // 對應 login.html 的 name="empPassword"
				.defaultSuccessUrl("/admin", false) // false：有原本要去的頁面就優先導回去
				.failureUrl("/admin/login?error"))
				.logout(logout -> logout.logoutUrl("/admin/logout") // 必須用 POST
				.logoutSuccessUrl("/admin/login?logout"))
				.exceptionHandling(ex -> ex.accessDeniedPage("/admin/403") // 已登入但權限不足 → 轉到 403 頁
				);
		return http.build();
	}
}
