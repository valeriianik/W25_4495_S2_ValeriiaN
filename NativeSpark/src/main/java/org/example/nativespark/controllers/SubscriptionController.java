//package org.example.nativespark.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//public class SubscriptionController {
//
//    @Autowired
//    private SubscriptionService subscriptionService;
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/subscription")
//    public String showSubscriptionForm(@RequestParam("userId") Long userId, @RequestParam("userType") String userType, Model model) {
//        model.addAttribute("userId", userId);
//        model.addAttribute("userType", userType);
//        return "subscription";
//    }
//}
