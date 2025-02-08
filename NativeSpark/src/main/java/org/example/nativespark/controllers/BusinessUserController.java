package org.example.nativespark.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.services.BusinessUserService;
import org.example.nativespark.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/register/business")
public class BusinessUserController {

    private final UserService userService;
    private final BusinessUserService businessUserService;

    @Autowired
    public BusinessUserController(UserService userService, BusinessUserService businessUserService) {
        this.userService = userService;
        this.businessUserService = businessUserService;
    }

    @PostMapping("/step1")
    public String registerBusinessUserStep1(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/register/business?error=password_mismatch";
        }

        // ✅ Save the user in the database
        User user = userService.createUser(email, password, "BUSINESS");
        session.setAttribute("registeredUser", user);

        return "redirect:/register/business/info";
    }

    @GetMapping("/info")
    public String showBusinessInfoForm() {
        return "business-info"; // ✅ Ensure this matches the Thymeleaf template name
    }

//    @PostMapping("/step2")
//    public String registerBusinessUserStep2(
//            @RequestParam String businessName,
//            @RequestParam String description,
//            @RequestParam("logo") MultipartFile logo,
//            HttpSession session) throws IOException {
//
//        User registeredUser = (User) session.getAttribute("registeredUser");
//        if (registeredUser == null) {
//            return "redirect:/register/business";
//        }
//
//        // ✅ Save Business User in Database
//        BusinessUser businessUser = businessUserService.registerBusinessUser(registeredUser, businessName, description, logo);
//
//        System.out.println("✅ Business User Registered: " + businessUser);
//
//        session.removeAttribute("registeredUser");
//        return "redirect:/login?success=business_registered";

    @PostMapping("/step2")
    public String registerBusinessUserStep2(
            @RequestParam String businessName,
            @RequestParam String description,
            @RequestParam("logo") MultipartFile logo,
            HttpSession session) throws IOException {

        User registeredUser = (User) session.getAttribute("registeredUser");

        if (registeredUser == null) {
            System.err.println("🚨 ERROR: No registered user found in session!");
            return "redirect:/register/business";
        }

        System.out.println("✅ User Found in Session: " + registeredUser.getEmail());

        // ✅ Save BusinessUser
        BusinessUser businessUser = businessUserService.registerBusinessUser(registeredUser, businessName, description, logo);

        System.out.println("✅ Business User Registered: " + businessUser);

        session.removeAttribute("registeredUser");
        return "redirect:/login?success=business_registered";
    }

}

