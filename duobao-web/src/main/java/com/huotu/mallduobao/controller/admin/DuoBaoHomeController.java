package com.huotu.mallduobao.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by zhang on 2016/3/31.
 */
@RequestMapping("/admin")
@Controller
public class DuoBaoHomeController {

    @RequestMapping("/home")
    public String home(@RequestParam("customerId")Long customerId,  Map<String, Object> map) {
        map.put("customerId", customerId);
        return "/duobao/home";
    }
}
