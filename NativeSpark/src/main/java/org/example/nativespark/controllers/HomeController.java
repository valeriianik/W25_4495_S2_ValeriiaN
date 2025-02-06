package org.example.nativespark.controllers;

//import com.example.nativesparkdemo.entities.Listing;
//import com.example.nativesparkdemo.entities.User;
//import com.example.nativesparkdemo.services.ListingService;
//import com.example.nativesparkdemo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

//    @Autowired
//    private ListingService listingService;
//
//    @Autowired
//    private UserService userService;

//    @GetMapping("/")
//    public String showHomePage(
//            @RequestParam(value = "category", required = false) String category,
//            Model model) {
//
//        List<Listing> listings;
//
//        if (category != null && !category.isEmpty()) {
//            listings = listingService.findByCategory(category);
//        } else {
//            listings = listingService.findAllWithPurchaseStatus();
//        }
//
//        listings.removeIf(Listing::isPurchased);
//
//        List<User> users = userService.findAll();
//        model.addAttribute("listings", listings);
//        model.addAttribute("users", users);
//        return "home";
//    }

    @GetMapping("/about")
    public String about() {

        return "welcome";
    }
}

