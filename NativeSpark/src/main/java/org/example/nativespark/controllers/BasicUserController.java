package org.example.nativespark.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.nativespark.entities.BasicUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.services.BasicUserService;
import org.example.nativespark.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/register/basic")
public class BasicUserController {

    private final UserService userService;
    private final BasicUserService basicUserService;

    @Autowired
    public BasicUserController(UserService userService, BasicUserService basicUserService) {
        this.userService = userService;
        this.basicUserService = basicUserService;
    }

    @PostMapping("/step1")
    public String registerBasicUserStep1(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/register/basic?error=password_mismatch";
        }

        User user = userService.createUser(email, password, "BASIC");
        session.setAttribute("registeredUser", user);

        return "redirect:/register/basic/info";
    }

    @GetMapping("/info")
    public String showBasicInfoForm() {
        return "basic-info";
    }

    @PostMapping("/step2")
    public String registerBasicUserStep2(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String about,
            @RequestParam("photo") MultipartFile photo,
            HttpSession session) throws IOException {

        User registeredUser = (User) session.getAttribute("registeredUser");
        if (registeredUser == null) {
            return "redirect:/register/basic";
        }

        BasicUser basicUser = basicUserService.registerBasicUser(registeredUser, firstName, lastName, about, photo);

        System.out.println("✅ Entrepreneur User Registered: " + basicUser);

        session.removeAttribute("registeredUser");
        return "redirect:/login?success=basic_registered";
    }
}
