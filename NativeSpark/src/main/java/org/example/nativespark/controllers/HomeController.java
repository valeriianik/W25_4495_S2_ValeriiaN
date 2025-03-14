package org.example.nativespark.controllers;

import org.example.nativespark.entities.Product;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.JobPostingRepository;
import org.example.nativespark.repositories.ProductRepository;
import org.example.nativespark.repositories.ProjectPostingRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ProjectPostingRepository projectPostingRepository;
    private final UserRepository userRepository;

    public HomeController(ProductRepository productRepository,
                          JobPostingRepository jobPostingRepository,
                          ProjectPostingRepository projectPostingRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.projectPostingRepository = projectPostingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // ✅ Always Fetch Products
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        // ✅ Set Default UserType for Guests
        String userType = "GUEST";

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userType = user.getUserType();
                model.addAttribute("userType", userType);

                if ("ENTREPRENEUR".equalsIgnoreCase(userType)) {
                    model.addAttribute("jobPostings", jobPostingRepository.findAll());
                    model.addAttribute("projectPostings", projectPostingRepository.findAll());
                }
            }
        }

        model.addAttribute("userType", userType);
        return "home";
    }

    @GetMapping("/about")
    public String about() {

        return "welcome";
    }

}

