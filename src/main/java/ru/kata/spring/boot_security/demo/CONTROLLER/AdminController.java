package ru.kata.spring.boot_security.demo.CONTROLLER;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.SERVICE.UserService;

import java.security.Principal;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model, Principal principal) {

        model.addAttribute("currentUser",
                userService.findByUsername(principal.getName()));

        return "admin";   // templates/admin.html
    }

    @GetMapping("/user")
    public String userPage(Model model, Principal principal) {

        model.addAttribute("currentUser",
                userService.findByUsername(principal.getName()));

        return "user";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
}