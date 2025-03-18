package org.example.nativespark.controllers;

import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.ProjectPosting;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.BusinessUserRepository;
import org.example.nativespark.repositories.ProjectPostingRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/project-postings")
public class ProjectPostingController {

    private final ProjectPostingRepository projectPostingRepository;
    private final BusinessUserRepository businessUserRepository;
    private final UserRepository userRepository;

    public ProjectPostingController(ProjectPostingRepository projectPostingRepository, BusinessUserRepository businessUserRepository, UserRepository userRepository) {
        this.projectPostingRepository = projectPostingRepository;
        this.businessUserRepository = businessUserRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public String showProjectPostingForm(Model model, Principal principal) {
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

        model.addAttribute("projectPosting", new ProjectPosting()); // ✅ Ensure this is set
        return "project-posting-form"; // ✅ Ensure this file exists in `templates/`
    }

    @PostMapping("/save")
    public String saveProjectPosting(ProjectPosting projectPosting, Principal principal) {
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
            projectPosting.setBusiness(businessUser.get());
            projectPosting.setPostedDate(LocalDateTime.now());
            projectPostingRepository.save(projectPosting);
        }

        return "redirect:/my_postings"; // Redirect back to postings page
    }

    @PostMapping("/delete/{id}")
    public String deleteProjectPosting(@PathVariable Long id) {
        projectPostingRepository.deleteById(id);
        return "redirect:/my_postings";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<ProjectPosting> projectPosting = projectPostingRepository.findById(id);

        if (projectPosting.isEmpty()) {
            return "redirect:/my_postings?error=ProjectNotFound";  // Redirect if not found
        }

        model.addAttribute("projectPosting", projectPosting.get());
        return "edit-project";
    }

    @PostMapping("/update/{id}")
    public String updateProjectPosting(@PathVariable Long id, @ModelAttribute ProjectPosting projectPosting) {
        Optional<ProjectPosting> existingProject = projectPostingRepository.findById(id);

        if (existingProject.isPresent()) {
            ProjectPosting updatedProject = existingProject.get();
            updatedProject.setProjectTitle(projectPosting.getProjectTitle());
            updatedProject.setBudget(projectPosting.getBudget());
            updatedProject.setDeadline(projectPosting.getDeadline());
            updatedProject.setRequiredSkills(projectPosting.getRequiredSkills());
            updatedProject.setProjectScope(projectPosting.getProjectScope());
            updatedProject.setProjectDescription(projectPosting.getProjectDescription());

            projectPostingRepository.save(updatedProject);
        }

        return "redirect:/my_postings";
    }


    @GetMapping("/{id}")
    public String viewProjectDetails(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        Optional<ProjectPosting> project = projectPostingRepository.findById(id);
        if (project.isEmpty()) {
            return "redirect:/?error=ProjectNotFound"; // Redirect if project is not found
        }

        model.addAttribute("project", project.get());
        return "project-details"; // Load project details Thymeleaf template
    }
}
