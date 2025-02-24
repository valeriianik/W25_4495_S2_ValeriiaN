package org.example.nativespark.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserRegistrationController {

    @GetMapping("/select-user-type")
    public String showUserTypeSelectionPage() {
        return "select-user-type";
    }

    @PostMapping("/register/user-type")
    public String selectUserType(@RequestParam("userType") String userType, HttpSession session) {
        session.setAttribute("selectedUserType", userType);

        if ("BUSINESS".equals(userType)) {
            return "redirect:/register/business";
        }
        else if ("ENTREPRENEUR".equals(userType)) {
            return "redirect:/register/entrepreneur";
        } else if ("BASIC".equals(userType)){
            return "redirect:/register/basic";
        }

        return "redirect:/register/basic";
    }

    @GetMapping("/register/business")
    public String showBusinessRegistrationForm() {
        return "business-register"; //
    }

    @GetMapping("/register/entrepreneur")
    public String showEntrepreneurRegistrationForm() {
        return "entrepreneur-register"; //
    }

    @GetMapping("/register/basic")
    public String showBasicRegistrationForm() {
        return "basic-register"; //
    }
}



