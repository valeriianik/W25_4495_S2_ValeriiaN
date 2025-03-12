package org.example.nativespark.controllers;

import org.example.nativespark.entities.JobPosting;
import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.JobPostingRepository;
import org.example.nativespark.repositories.BusinessUserRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/job-postings")
public class JobPostingController {

    private final JobPostingRepository jobPostingRepository;
    private final BusinessUserRepository businessUserRepository;
    private final UserRepository userRepository;

    public JobPostingController(JobPostingRepository jobPostingRepository, BusinessUserRepository businessUserRepository, UserRepository userRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.businessUserRepository = businessUserRepository;
        this.userRepository = userRepository;
    }

    // ✅ Show the job posting creation form
    @GetMapping("/create")
    public String showJobPostingForm(Model model, Principal principal) {
        // Get the authenticated user's email
        String email = principal.getName();

        // Find User
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "redirect:/account"; // Redirect if user is not found
        }

        // Find BusinessUser associated with this User
        Optional<BusinessUser> businessUser = businessUserRepository.findByUser(user.get());

        if (businessUser.isEmpty()) {
            return "redirect:/account"; // Redirect if user is NOT a business user
        }

        model.addAttribute("jobPosting", new JobPosting()); // ✅ Ensure this is set
        return "job-posting-form"; // ✅ Ensure this file exists in `templates/`
    }

    // ✅ Handle job posting form submission
    @PostMapping("/save")
    public String saveJobPosting(JobPosting jobPosting, Principal principal) {
        // Get authenticated user email
        String email = principal.getName();

        // Find User
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "redirect:/account"; // Redirect if user is not found
        }

        // Find BusinessUser associated with this User
        Optional<BusinessUser> businessUser = businessUserRepository.findByUser(user.get());

        if (businessUser.isPresent()) {
            jobPosting.setBusiness(businessUser.get());
            jobPosting.setPostedDate(LocalDateTime.now());
            jobPostingRepository.save(jobPosting);
        }

        return "redirect:/my_postings"; // Redirect back to postings page
    }

//    @PostMapping("/delete/{id}")
//    public String deleteJobPosting(@PathVariable Long id) {
//        jobPostingRepository.deleteById(id);
//        return "redirect:/my_postings";
//    }

}
