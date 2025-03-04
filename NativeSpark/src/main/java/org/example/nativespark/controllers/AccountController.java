package org.example.nativespark.controllers;

import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.context.SecurityContextHolder;



import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showAccountPage(Model model) {
        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ No authenticated user found, redirecting to login.");
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            System.out.println("❌ User not found in database, redirecting to login.");
            return "redirect:/login";
        }

        User user = userOptional.get();
        model.addAttribute("user", user);
        System.out.println("✅ User session loaded successfully: " + user.getEmail());

        return "account";
    }
}
