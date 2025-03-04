package org.example.nativespark.controllers;

import org.example.nativespark.entities.User;
import org.example.nativespark.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            Model model) {

        System.out.println("🔄 Attempting login for email: " + email);

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            System.out.println("❌ User not found: " + email);
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        User user = userOptional.get();
        if (!userService.verifyPassword(password, user.getPassword())) {
            System.out.println("❌ Incorrect password for: " + email);
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        System.out.println("✅ Authentication successful. Redirecting to account...");
        return "redirect:/account";
    }
}

