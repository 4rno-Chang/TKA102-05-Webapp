package com.bistroops;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import com.bistroops.security.admin.AdminUserDetails;


// 後台(廠商)首頁，後台頁面統一放在 /admin 底下
@Controller
@RequestMapping("/admin")
public class AdminIndexController {

        @GetMapping("")
    public String index(@AuthenticationPrincipal AdminUserDetails loginEmp, Model model) {
        model.addAttribute("loginEmp", loginEmp); // 目前登入的員工
        return "admin/index"; //view
    }


    @GetMapping("/login")
	 public String login(){
		return "admin/login";
	}
}
