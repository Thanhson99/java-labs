package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final String applicationName;
    private final String serverPort;

    public WebController(
            @Value("${spring.application.name:spring}") String applicationName,
            @Value("${server.port:8089}") String serverPort) {
        this.applicationName = applicationName;
        this.serverPort = serverPort;
    }

    @GetMapping("/")
    public String greet(@RequestParam(value = "name", defaultValue = "ThanhSon99") String name, Model model) {
        populateCommonModel(model);
        model.addAttribute("name", name);
        return "index";
    }

    @GetMapping("/admin")
    public String adminConsole(Model model) {
        populateCommonModel(model);
        model.addAttribute("name", "ThanhSon99");
        return "admin";
    }

    private void populateCommonModel(Model model) {
        model.addAttribute("applicationName", applicationName);
        model.addAttribute("serverPort", serverPort);
    }
}
