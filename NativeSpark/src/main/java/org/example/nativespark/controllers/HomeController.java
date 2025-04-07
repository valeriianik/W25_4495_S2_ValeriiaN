package org.example.nativespark.controllers;

import org.example.nativespark.entities.JobPosting;
import org.example.nativespark.entities.Product;
import org.example.nativespark.entities.ProjectPosting;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.JobPostingRepository;
import org.example.nativespark.repositories.ProductRepository;
import org.example.nativespark.repositories.ProjectPostingRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

//    @GetMapping("/")
//    public String home(@RequestParam(value = "keyword", required = false) String keyword,
//                       Model model, Authentication authentication) {
//
//        List<Product> products;
//        List<JobPosting> jobPostings;
//        List<ProjectPosting> projectPostings;
//
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            products = productRepository.findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(keyword, keyword);
//            jobPostings = jobPostingRepository.findByJobDescriptionContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCase(keyword, keyword);
//            projectPostings = projectPostingRepository.findByProjectDescriptionContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCase(keyword, keyword);
//        } else {
//            products = productRepository.findAll();
//            jobPostings = jobPostingRepository.findAll();
//            projectPostings = projectPostingRepository.findAll();
//        }
//
//        model.addAttribute("products", products);
//        model.addAttribute("jobPostings", jobPostings);
//        model.addAttribute("projectPostings", projectPostings);
//        model.addAttribute("keyword", keyword); // Optional: reuse in search box
//
//        Long loggedInUserId = null;
//        String userType = "GUEST";
//
//        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
//            String email = authentication.getName();
//            Optional<User> userOptional = userRepository.findByEmail(email);
//
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                userType = user.getUserType();
//                loggedInUserId = user.getUserId();
//                model.addAttribute("userType", userType);
//                model.addAttribute("loggedInUserId", loggedInUserId);
//                model.addAttribute("loggedInUser", user);
//            }
//        }
//
//        model.addAttribute("userType", userType);
//        return "home";
//    }

    @GetMapping("/")
    public String home(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "category", required = false) String category,
                       @RequestParam(value = "employmentType", required = false) String employmentType,
                       Model model, Authentication authentication) {

        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepository.findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(keyword, keyword);
        } else {
            products = productRepository.findAll();
        }

        if (category != null && !category.isEmpty()) {
            products.removeIf(p -> !p.getCategory().equalsIgnoreCase(category));
        }

        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        if (employmentType != null && !employmentType.isEmpty()) {
            jobPostings.removeIf(j -> !j.getEmploymentType().equalsIgnoreCase(employmentType));
        }

        model.addAttribute("products", products);
        model.addAttribute("jobPostings", jobPostings);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedEmploymentType", employmentType);
        model.addAttribute("allCategories", productRepository.findDistinctCategories()); // Add this method

        Long loggedInUserId = null;
        String userType = "GUEST";

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userType = user.getUserType();
                loggedInUserId = user.getUserId();
                model.addAttribute("userType", userType);
                model.addAttribute("loggedInUserId", loggedInUserId);
                model.addAttribute("loggedInUser", user);

                if ("ENTREPRENEUR".equalsIgnoreCase(userType)) {
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

