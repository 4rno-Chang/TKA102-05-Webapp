package com.bistroops;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 後台(廠商)首頁，後台頁面統一放在 /staff 底下
@Controller
@RequestMapping("/staff")
public class StaffLoginController {

	

	@GetMapping("/login")
	public String login() {
		return "staff/login";
	}

	// 權限不足頁（StaffSecurityConfig 的 accessDeniedPage 會轉到這裡）
	@RequestMapping("/403")
	public String accessDenied() {
		return "staff/403";
	}

}
