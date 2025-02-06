package org.example.nativespark.controllers;

//import com.example.nativespark.entities.User;
//import com.example.nativespark.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

//    @Autowired
//    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }

//    @PostMapping("/login")
//    public String loginUser(@RequestParam("email") String email,
//                            @RequestParam("password") String password,
//                            HttpSession session, Model model) {
//        boolean authenticated = userService.authenticateUser(email, password);
//        if (authenticated) {
//            User user = userService.findByEmail(email);
//
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), List.of(() -> "USER"));
//
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//            session.setAttribute("userEmail", user.getEmail());
//
//            return "redirect:/account";
//        } else {
//            model.addAttribute("error", "Invalid email or password. Please try again.");
//            return "login";
//        }
    }
