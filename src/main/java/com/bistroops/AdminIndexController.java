package com.bistroops;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 後台(廠商)首頁，後台頁面統一放在 /admin 底下
@Controller
@RequestMapping("/admin")
public class AdminIndexController {

    @GetMapping("")
    public String index() {
        return "admin/index"; //view
    }

}
