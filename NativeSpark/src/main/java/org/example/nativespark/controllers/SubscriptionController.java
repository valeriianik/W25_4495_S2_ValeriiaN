package org.example.nativespark.controllers;

import org.example.nativespark.entities.Subscription;
import org.example.nativespark.entities.Transaction;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.SubscriptionRepository;
import org.example.nativespark.repositories.TransactionRepository;
import org.example.nativespark.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/subscription")
    public String showSubscriptionPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();
        model.addAttribute("user", user);

        Optional<Subscription> subscriptionOptional = subscriptionRepository.findByUser(user);
        if (subscriptionOptional.isPresent()) {
            model.addAttribute("subscription", subscriptionOptional.get());
        } else {
            // Assign a free subscription if user has no subscription
            Subscription freeSubscription = new Subscription(user, "Free");
            subscriptionRepository.save(freeSubscription);
            model.addAttribute("subscription", freeSubscription);
        }

        return "subscription";
    }

    @PostMapping("/update-subscription")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateSubscription(@RequestParam("subscriptionType") String subscriptionType) {
        Map<String, String> response = new HashMap<>();

        // ✅ Log received subscription type
        logger.info("Received subscription update request: " + subscriptionType);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated.");
            response.put("status", "error");
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            logger.error("User not found in the database: " + email);
            response.put("status", "error");
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findByUser(user);

        // Determine cost based on subscription type
        double cost = 0.0;
        if ("Basic".equalsIgnoreCase(subscriptionType)) {
            cost = 9.99;
        } else if ("Premium".equalsIgnoreCase(subscriptionType)) {
            cost = 19.99;
        }

        if (subscriptionOptional.isPresent()) {
            Subscription subscription = subscriptionOptional.get();
            logger.info("Updating subscription for user: " + user.getEmail() + " to " + subscriptionType);
            subscription.setSubscriptionType(subscriptionType);
            subscriptionRepository.save(subscription);
        } else {
            logger.info("Creating new subscription for user: " + user.getEmail());
            Subscription newSubscription = new Subscription(user, subscriptionType);
            subscriptionRepository.save(newSubscription);
        }

        // Store transaction ONLY if the user upgrades to a paid subscription
        if (cost > 0) {
            Transaction transaction = new Transaction(user, subscriptionType, cost);
            transactionRepository.save(transaction);
            logger.info("Transaction recorded: " + transaction.getAmount() + " for " + transaction.getSubscriptionType());
        }

        response.put("status", "success");
        response.put("message", "Subscription updated successfully");
        return ResponseEntity.ok(response);
    }
}


