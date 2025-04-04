package org.example.nativespark.controllers;

import org.example.nativespark.entities.CustomProductRequest;
import org.example.nativespark.entities.EntrepreneurUser;
import org.example.nativespark.entities.Product;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.CustomProductRequestRepository;
import org.example.nativespark.repositories.EntrepreneurUserRepository;
import org.example.nativespark.repositories.ProductRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/request-custom")
public class CustomProductRequestController {

    private final ProductRepository productRepository;
    private final CustomProductRequestRepository customProductRequestRepository;
    private final UserRepository userRepository;
    private final EntrepreneurUserRepository entrepreneurUserRepository;

    @Autowired
    public CustomProductRequestController(ProductRepository productRepository,
                                          CustomProductRequestRepository customProductRequestRepository,
                                          UserRepository userRepository,
                                          EntrepreneurUserRepository entrepreneurUserRepository) {
        this.productRepository = productRepository;
        this.customProductRequestRepository = customProductRequestRepository;
        this.userRepository = userRepository;
        this.entrepreneurUserRepository = entrepreneurUserRepository;
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

    @GetMapping("/my_requests")
    public String viewMyCustomRequests(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        List<CustomProductRequest> customRequests = customProductRequestRepository.findByUser(user);
        model.addAttribute("customRequests", customRequests);
        return "my_requests";
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('ROLE_ENTREPRENEUR')")
    public String viewRequestsForMyProducts(Model model, Authentication authentication) {
        User entrepreneurUser = userRepository.findByEmail(authentication.getName()).orElseThrow();
        EntrepreneurUser entrepreneur = entrepreneurUserRepository.findByUser(entrepreneurUser).orElseThrow();

        List<CustomProductRequest> allRequests = customProductRequestRepository.findAllByProduct_Entrepreneur(entrepreneur);
        model.addAttribute("requests", allRequests);

        return "requests"; // ✅ This must match the name of your Thymeleaf template (requests.html)
    }

}

