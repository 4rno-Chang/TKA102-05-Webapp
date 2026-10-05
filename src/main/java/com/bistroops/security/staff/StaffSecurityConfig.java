package com.bistroops.security.staff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

// 後台 /staff/** 的安全設定
@Configuration
public class StaffSecurityConfig {

	@Value("${bistroops.staff.security-enabled:true}") // 沒設定時預設 true（比較安全）
	private boolean securityEnabled;
	
	private final StaffUserDetailsService staffUserDetailsService;

	public StaffSecurityConfig(StaffUserDetailsService staffUserDetailsService) {
		this.staffUserDetailsService = staffUserDetailsService;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain staffFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/staff/**");

		if (securityEnabled) {
			http.authorizeHttpRequests(auth -> auth
					.requestMatchers("/staff/login")
					.permitAll()     // 登入頁一定要放行，否則會無限重導
					.requestMatchers("/staff/announcement/**")
					.hasAuthority("PERM_5") // 5 = 公告設定
					.anyRequest()
					.authenticated());     // 其他後台頁面：登入後才能進
		} else {
			http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 開發模式：全部放行
		}

		http.userDetailsService(staffUserDetailsService)
				.formLogin(form -> form.loginPage("/staff/login") // GET：顯示登入頁（StaffLoginController）
				.loginProcessingUrl("/staff/login") // POST：由 Security 處理帳密比對，不用寫 Controller
				.usernameParameter("empNo") // 對應 login.html 的 name="empNo"
				.passwordParameter("empPassword") // 對應 login.html 的 name="empPassword"
				.defaultSuccessUrl("/staff", false) // false：有原本要去的頁面就優先導回去
				.failureUrl("/staff/login?error"))
				.logout(logout -> logout.logoutUrl("/staff/logout") // 必須用 POST
				.logoutSuccessUrl("/staff/login?logout"))
				.exceptionHandling(ex -> ex.accessDeniedPage("/staff/403") // 已登入但權限不足 → 轉到 403 頁
				);
		return http.build();
	}
}
