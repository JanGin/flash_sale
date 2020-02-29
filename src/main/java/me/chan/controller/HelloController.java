package me.chan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/test")
    public String test(Model model) {
        model.addAttribute("kobe", "Kobe Bryant");
        return "hello";
    }
}
