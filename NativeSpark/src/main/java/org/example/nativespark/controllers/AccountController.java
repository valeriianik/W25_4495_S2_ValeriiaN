package org.example.nativespark.controllers;

import org.example.nativespark.entities.*;
import org.example.nativespark.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntrepreneurUserRepository entrepreneurUserRepository;

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private BasicUserRepository basicUserRepository;

    @GetMapping
    public String showAccountPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
        System.out.println("✅ Logged-in User Type: " + user.getUserType());

        // Load user-specific details
        switch (user.getUserType().toLowerCase()) {
            case "entrepreneur":
                Optional<EntrepreneurUser> entrepreneurUser = entrepreneurUserRepository.findByUser(user);
                if (entrepreneurUser.isPresent()) {
                    model.addAttribute("entrepreneur", entrepreneurUser.get());
                    System.out.println("✅ Entrepreneur User loaded: " + entrepreneurUser.get().getFirstName());
                } else {
                    System.out.println("❌ Entrepreneur User NOT FOUND for user: " + email);
                }
                break;

            case "business":
                Optional<BusinessUser> businessUser = businessUserRepository.findByUser(user);
                if (businessUser.isPresent()) {
                    model.addAttribute("business", businessUser.get());
                    System.out.println("✅ Business User loaded: " + businessUser.get().getBusinessName());
                } else {
                    System.out.println("❌ Business User NOT FOUND for user: " + email);
                }
                break;

            case "basic":
                Optional<BasicUser> basicUser = basicUserRepository.findByUser(user);
                if (basicUser.isPresent()) {
                    model.addAttribute("basic", basicUser.get());
                    System.out.println("✅ Basic User loaded: " + basicUser.get().getFirstName());
                } else {
                    System.out.println("❌ Basic User NOT FOUND for user: " + email);
                }
                break;

            default:
                System.out.println("❌ Unknown user type: " + user.getUserType());
        }

        return "account";
    }
}




