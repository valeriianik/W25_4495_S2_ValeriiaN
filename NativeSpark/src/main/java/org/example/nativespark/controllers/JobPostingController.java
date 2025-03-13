package org.example.nativespark.controllers;

import org.example.nativespark.entities.JobPosting;
import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.JobPostingRepository;
import org.example.nativespark.repositories.BusinessUserRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/delete/{id}")
    public String deleteJobPosting(@PathVariable Long id) {
        jobPostingRepository.deleteById(id);
        return "redirect:/my_postings";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<JobPosting> jobPosting = jobPostingRepository.findById(id);

        if (jobPosting.isEmpty()) {
            return "redirect:/my_postings?error=JobNotFound";  // Redirect if job does not exist
        }

        model.addAttribute("jobPosting", jobPosting.get());
        return "edit-job";  // ✅ Ensure this Thymeleaf template exists in `src/main/resources/templates`
    }

    @PostMapping("/update/{id}")
    public String updateJobPosting(@PathVariable Long id, @ModelAttribute JobPosting jobPosting) {
        Optional<JobPosting> existingJob = jobPostingRepository.findById(id);

        if (existingJob.isPresent()) {
            JobPosting updatedJob = existingJob.get();
            updatedJob.setJobTitle(jobPosting.getJobTitle());
            updatedJob.setLocation(jobPosting.getLocation());
            updatedJob.setEmploymentType(jobPosting.getEmploymentType());
            updatedJob.setSalary(jobPosting.getSalary());
            updatedJob.setRequiredExperience(jobPosting.getRequiredExperience());
            updatedJob.setRequiredSkills(jobPosting.getRequiredSkills());
            updatedJob.setJobDescription(jobPosting.getJobDescription());

            jobPostingRepository.save(updatedJob);
        }

        return "redirect:/my_postings";  // Redirect after successful update
    }
}
