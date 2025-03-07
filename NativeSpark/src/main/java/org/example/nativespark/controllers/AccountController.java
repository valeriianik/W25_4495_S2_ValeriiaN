package org.example.nativespark.controllers;

import org.example.nativespark.entities.*;
import org.example.nativespark.repositories.*;
import org.example.nativespark.services.BasicUserService;
import org.example.nativespark.services.BusinessUserService;
import org.example.nativespark.services.EntrepreneurUserService;
import org.example.nativespark.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserRepository userRepository;
    private final EntrepreneurUserRepository entrepreneurUserRepository;
    private final BusinessUserRepository businessUserRepository;
    private final BasicUserRepository basicUserRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final BusinessUserService businessUserService;
    private final EntrepreneurUserService entrepreneurUserService;
    private final BasicUserService basicUserService;

    @Autowired
    public AccountController(
            UserRepository userRepository,
            EntrepreneurUserRepository entrepreneurUserRepository,
            BusinessUserRepository businessUserRepository,
            BasicUserRepository basicUserRepository,
            SubscriptionRepository subscriptionRepository,
            UserService userService,
            BusinessUserService businessUserService,
            EntrepreneurUserService entrepreneurUserService,
            BasicUserService basicUserService) {
        this.userRepository = userRepository;
        this.entrepreneurUserRepository = entrepreneurUserRepository;
        this.businessUserRepository = businessUserRepository;
        this.basicUserRepository = basicUserRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
        this.businessUserService = businessUserService;
        this.entrepreneurUserService = entrepreneurUserService;
        this.basicUserService = basicUserService;
    }

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
                entrepreneurUserRepository.findByUser(user)
                        .ifPresent(entrepreneur -> {
                            model.addAttribute("entrepreneur", entrepreneur);
                            System.out.println("✅ Entrepreneur User loaded: " + entrepreneur.getFirstName());
                        });
                break;

            case "business":
                businessUserRepository.findByUser(user)
                        .ifPresent(business -> {
                            model.addAttribute("business", business);
                            System.out.println("✅ Business User loaded: " + business.getBusinessName());
                        });
                break;

            case "basic":
                basicUserRepository.findByUser(user)
                        .ifPresent(basic -> {
                            model.addAttribute("basic", basic);
                            System.out.println("✅ Basic User loaded: " + basic.getFirstName());
                        });
                break;

            default:
                System.out.println("❌ Unknown user type: " + user.getUserType());
        }

        // Fetch the user's subscription
        subscriptionRepository.findByUser(user)
                .ifPresent(subscription -> model.addAttribute("subscription", subscription));

        return "account";
    }

    @PostMapping("/update")
    public String updateUser(
            @AuthenticationPrincipal UserDetails loggedInUser,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String identityType,
            @RequestParam(required = false) String about,
            @RequestParam(required = false) String businessName,
            @RequestParam(required = false) String description,
            @RequestParam(value = "logo", required = false) MultipartFile logoPath,
            @RequestParam(value = "photo", required = false) MultipartFile photoPath,
            Model model) throws IOException {

        Optional<User> optionalUser = userService.findByEmail(loggedInUser.getUsername());

        if (optionalUser.isEmpty()) {
            return "redirect:/account?error=user_not_found";
        }

        User user = optionalUser.get();
        String userType = user.getUserType();

        switch (userType.toLowerCase()) {
            case "business":
                BusinessUser updatedBusinessUser = businessUserService.updateBusinessUser(user, businessName, description, logoPath);
                model.addAttribute("business", updatedBusinessUser);
                break;
            case "entrepreneur":
                EntrepreneurUser updatedEntrepreneurUser = entrepreneurUserService.updateEntrepreneurUser(user, firstName, lastName, identityType, about, photoPath);
                model.addAttribute("entrepreneur", updatedEntrepreneurUser);
                break;
            case "basic":
                BasicUser updatedBasicUser = basicUserService.updateBasicUser(user, firstName, lastName, about, photoPath);
                model.addAttribute("basic", updatedBasicUser);
                break;
        }

        return "redirect:/account?success=profile_updated";
    }

}

