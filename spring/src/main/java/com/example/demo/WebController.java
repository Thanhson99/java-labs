package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the public landing page and the lightweight admin console page.
 */
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

    /**
     * Renders the public learning dashboard.
     *
     * @param name  name rendered in the hero section
     * @param model MVC model populated for the Thymeleaf template
     * @return the public home template name
     */
    @GetMapping("/")
    public String homePage(@RequestParam(value = "name", defaultValue = "ThanhSon99") String name, Model model) {
        populateCommonModel(model);
        model.addAttribute("name", name);
        return "index";
    }

    /**
     * Renders the browser-based admin console shell.
     *
     * @param model MVC model populated for the Thymeleaf template
     * @return the admin template name
     */
    @GetMapping("/admin")
    public String adminConsole(Model model) {
        populateCommonModel(model);
        model.addAttribute("name", "ThanhSon99");
        return "admin";
    }

    /**
     * Renders the Java basics cheat sheet page.
     *
     * @param model MVC model populated for the Thymeleaf template
     * @return the cheat sheet template name
     */
    @GetMapping("/cheatsheets/java-basics")
    public String javaBasicsCheatSheet(Model model) {
        populateCommonModel(model);
        model.addAttribute("name", "ThanhSon99");
        return "cheatsheet";
    }

    /**
     * Renders the learning roadmap page.
     *
     * @param model MVC model populated for the Thymeleaf template
     * @return the roadmap template name
     */
    @GetMapping("/roadmap")
    public String roadmap(Model model) {
        populateCommonModel(model);
        model.addAttribute("name", "ThanhSon99");
        return "roadmap";
    }

    /**
     * Adds shared runtime metadata used by multiple HTML pages.
     *
     * @param model MVC model to enrich
     */
    private void populateCommonModel(Model model) {
        model.addAttribute("applicationName", applicationName);
        model.addAttribute("serverPort", serverPort);
    }
}
