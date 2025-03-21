package org.example.nativespark.controllers;

import org.example.nativespark.entities.CustomProductRequest;
import org.example.nativespark.entities.Product;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.CustomProductRequestRepository;
import org.example.nativespark.repositories.ProductRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/request-custom")
public class CustomProductRequestController {

    private final ProductRepository productRepository;
    private final CustomProductRequestRepository customProductRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomProductRequestController(ProductRepository productRepository,
                                          CustomProductRequestRepository customProductRequestRepository,
                                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.customProductRequestRepository = customProductRequestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public String showCustomRequestForm(@PathVariable Long id, @RequestParam(value = "success", required = false) String success, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("request", new CustomProductRequest());

        if ("true".equals(success)) {
            model.addAttribute("message", "Your request has been submitted successfully!");
        }

        return "request_custom_amount";
    }

    @PostMapping("/{id}")
    public String submitCustomRequest(@PathVariable Long id,
                                      @ModelAttribute("request") CustomProductRequest request,
                                      Authentication auth) {
        Product product = productRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        request.setProduct(product);
        request.setUser(user);
        customProductRequestRepository.save(request);

        return "redirect:/request-custom/" + id + "?success=true";
    }
}

