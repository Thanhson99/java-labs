package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String greet(@RequestParam(value = "name", defaultValue = "test") String name, Model model) {
        model.addAttribute("name", name);
        return "index"; // Trả về tệp index.html trong thư mục templates
    }
}
