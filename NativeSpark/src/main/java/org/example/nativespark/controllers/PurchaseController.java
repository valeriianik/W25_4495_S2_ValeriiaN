package org.example.nativespark.controllers;

import org.example.nativespark.entities.*;
import org.example.nativespark.repositories.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/{id}")
    public String showPurchasePage(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/?error=ProductNotFound";
        }

        Product product = productOpt.get();

        // ✅ Ensure entrepreneur and user data are loaded
        Hibernate.initialize(product.getEntrepreneur().getUser());

        model.addAttribute("product", product);
        return "purchase";  // ✅ Link to Thymeleaf purchase page
    }

    @PostMapping("/confirm")
    public String processPurchase(
            @RequestParam Long productId,
            @RequestParam Long sellerId,
            @RequestParam int quantity,
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam String province,
            @RequestParam String streetAddress,
            @RequestParam String postalCode,
            @RequestParam String phoneNumber,
            @RequestParam String cardNumber,
            @RequestParam String expiryDate,
            @RequestParam String cvv,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<User> buyerOpt = userRepository.findByEmail(principal.getName());
        Optional<User> sellerOpt = userRepository.findById(sellerId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (buyerOpt.isEmpty() || sellerOpt.isEmpty() || productOpt.isEmpty()) {
            model.addAttribute("error", "Invalid transaction data.");
            return "error";
        }

        User buyer = buyerOpt.get();
        User seller = sellerOpt.get();
        Product product = productOpt.get();

        if (quantity > product.getQuantity()) {
            model.addAttribute("error", "Not enough stock available.");
            return "error";
        }

        // ✅ Calculate total price
        double totalPrice = product.getPrice() * quantity;

        // ✅ Update product stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // ✅ Save transaction
        Transaction transaction = Transaction.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .quantity(quantity)
                .totalPrice(product.getPrice() * quantity)
                .country(country)
                .city(city)
                .province(province)
                .streetAddress(streetAddress)
                .postalCode(postalCode)
                .phoneNumber(phoneNumber)
                .status("SUCCESS")
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        model.addAttribute("transactionId", transaction.getId());  // Pass order ID to Thymeleaf
        model.addAttribute("totalPrice", totalPrice);
        return "purchase-success";  // ✅ Redirect to success page
    }
}
