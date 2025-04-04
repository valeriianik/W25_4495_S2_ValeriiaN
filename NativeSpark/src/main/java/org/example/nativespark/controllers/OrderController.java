package org.example.nativespark.controllers;

import org.example.nativespark.entities.Transaction;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.TransactionRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public OrderController(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my_orders")
    public String viewOrders(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Transaction> orders = transactionRepository.findByBuyer(user);
            model.addAttribute("orders", orders);
        }

        return "my_orders";
    }

//    @GetMapping("/buyers")
//    public String viewBuyersOfMyProducts(Model model, Authentication authentication) {
//        String email = authentication.getName();
//        User user = userRepository.findByEmail(email).orElse(null);
//
//        List<Transaction> sales = transactionRepository.findByBuyer(user);
//        model.addAttribute("sales", sales);
//
//        return "buyers";
//    }

    @GetMapping("/buyers")
    public String viewBuyersOfMyProducts(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        List<Transaction> sales = transactionRepository.findBySeller(user);  // ✅ correct lookup
        model.addAttribute("sales", sales);

        return "buyers";
    }

}
