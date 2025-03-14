package org.example.nativespark.controllers;

import org.example.nativespark.entities.*;
        import org.example.nativespark.repositories.*;
        import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class MyPostingsController {

    private final JobPostingRepository jobPostingRepository;
    private final ProjectPostingRepository projectPostingRepository;
    private final ProductRepository productRepository;
    private final BusinessUserRepository businessUserRepository;
    private final EntrepreneurUserRepository entrepreneurUserRepository;
    private final UserRepository userRepository;

    public MyPostingsController(JobPostingRepository jobPostingRepository,
                                ProjectPostingRepository projectPostingRepository,
                                ProductRepository productRepository,
                                BusinessUserRepository businessUserRepository,
                                EntrepreneurUserRepository entrepreneurUserRepository,
                                UserRepository userRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.projectPostingRepository = projectPostingRepository;
        this.productRepository = productRepository;
        this.businessUserRepository = businessUserRepository;
        this.entrepreneurUserRepository = entrepreneurUserRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my_postings")
    public String showMyPostings(Model model) {
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

        if (user.getUserType().equalsIgnoreCase("BUSINESS")) {
            Optional<BusinessUser> businessUserOptional = businessUserRepository.findByUser(user);

            if (businessUserOptional.isPresent()) {
                BusinessUser businessUser = businessUserOptional.get();

                // ✅ Fetch job and project postings using BusinessUser
                List<JobPosting> jobPostings = jobPostingRepository.findByBusiness(businessUser);
                List<ProjectPosting> projectPostings = projectPostingRepository.findByBusiness(businessUser);

                model.addAttribute("jobPostings", jobPostings);
                model.addAttribute("projectPostings", projectPostings);
            }
        } else if (user.getUserType().equalsIgnoreCase("ENTREPRENEUR")) {
            Optional<EntrepreneurUser> entrepreneurUserOptional = entrepreneurUserRepository.findByUser(user);

            if (entrepreneurUserOptional.isPresent()) {
                EntrepreneurUser entrepreneurUser = entrepreneurUserOptional.get();

                // ✅ Fetch products using EntrepreneurUser
                List<Product> products = productRepository.findByEntrepreneur(entrepreneurUser);
                model.addAttribute("products", products);
            }
        } else {
            return "redirect:/account"; // ✅ Basic users should not access this page
        }

        return "my_postings";
    }
}

