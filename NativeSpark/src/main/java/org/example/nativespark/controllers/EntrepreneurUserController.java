package org.example.nativespark.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.nativespark.entities.EntrepreneurUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.services.EntrepreneurUserService;
import org.example.nativespark.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/register/entrepreneur")
public class EntrepreneurUserController {

    private final UserService userService;
    private final EntrepreneurUserService entrepreneurUserService;

    @Autowired
    public EntrepreneurUserController(UserService userService, EntrepreneurUserService entrepreneurUserService) {
        this.userService = userService;
        this.entrepreneurUserService = entrepreneurUserService;
    }

    @PostMapping("/step1")
    public String registerEntrepreneurUserStep1(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/register/entrepreneur?error=password_mismatch";
        }

        User user = userService.createUser(email, password, "ENTREPRENEUR");
        session.setAttribute("registeredUser", user);

        return "redirect:/register/entrepreneur/info";
    }

    @GetMapping("/info")
    public String showEntrepreneurInfoForm() {
        return "entrepreneur-info";
    }

    @PostMapping("/step2")
    public String registerEntrepreneurUserStep2(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String about,
            @RequestParam String identityType,
            @RequestParam("photo") MultipartFile photo,
            HttpSession session) throws IOException {

        User registeredUser = (User) session.getAttribute("registeredUser");
        if (registeredUser == null) {
            return "redirect:/register/entrepreneur";
        }

        EntrepreneurUser entrepreneurUser = entrepreneurUserService.registerEntrepreneurUser(registeredUser, firstName, lastName, about, identityType, photo);

        System.out.println("✅ Entrepreneur User Registered: " + entrepreneurUser);

        session.removeAttribute("registeredUser");
        return "redirect:/login?success=entrepreneur_registered";
    }
}

