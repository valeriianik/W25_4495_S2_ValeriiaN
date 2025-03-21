package org.example.nativespark.controllers;

import org.example.nativespark.entities.JobPosting;
import org.example.nativespark.entities.Product;
import org.example.nativespark.entities.ProjectPosting;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.JobPostingRepository;
import org.example.nativespark.repositories.ProductRepository;
import org.example.nativespark.repositories.ProjectPostingRepository;
import org.example.nativespark.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

@Controller
public class SaveController {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final JobPostingRepository jobPostingRepo;
    private final ProjectPostingRepository projectPostingRepo;

    public SaveController(ProductRepository productRepo, UserRepository userRepo, JobPostingRepository jobPostingRepo,
    ProjectPostingRepository projectPostingRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.jobPostingRepo = jobPostingRepo;
        this.projectPostingRepo = projectPostingRepo;
    }

    @PostMapping("/product/{id}/toggle-save")
    @ResponseBody
    public ResponseEntity<String> toggleSave(@PathVariable Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        Product product = productRepo.findById(id).orElse(null);
        if (product == null) return ResponseEntity.notFound().build();

        Set<User> savedUsers = product.getSavedByUsers();
        String responseStatus;
        if (savedUsers.contains(user)) {
            savedUsers.remove(user);
            responseStatus = "unsaved";
        } else {
            savedUsers.add(user);
            responseStatus = "saved";
        }

        productRepo.save(product);
        return ResponseEntity.ok(responseStatus);
    }


    @GetMapping("/saved")
    public String saved(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        List<Product> savedProducts = productRepo.findAllBySavedByUsersContaining(user);
        List<JobPosting> savedJobs = jobPostingRepo.findAllBySavedByUsersContaining(user);
        List<ProjectPosting> savedProjects = projectPostingRepo.findAllBySavedByUsersContaining(user);

        model.addAttribute("savedProducts", savedProducts);
        model.addAttribute("savedJobs", savedJobs);
        model.addAttribute("savedProjects", savedProjects);

        model.addAttribute("loggedInUser", user);//new


        return "saved"; // saved.html
    }

    @PostMapping("/job/{id}/toggle-save")
    @ResponseBody
    public ResponseEntity<String> toggleSaveJob(@PathVariable Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        JobPosting job = jobPostingRepo.findById(id).orElse(null);
        if (job == null) return ResponseEntity.notFound().build();

        Set<User> savedUsers = job.getSavedByUsers();
        String responseStatus;
        if (savedUsers.contains(user)) {
            savedUsers.remove(user);
            responseStatus = "unsaved";
        } else {
            savedUsers.add(user);
            responseStatus = "saved";
        }

        jobPostingRepo.save(job);
        return ResponseEntity.ok(responseStatus);
    }

    @PostMapping("/project/{id}/toggle-save")
    @ResponseBody
    public ResponseEntity<String> toggleSaveProject(@PathVariable Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        ProjectPosting project = projectPostingRepo.findById(id).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        Set<User> savedUsers = project.getSavedByUsers();
        String responseStatus;
        if (savedUsers.contains(user)) {
            savedUsers.remove(user);
            responseStatus = "unsaved";
        } else {
            savedUsers.add(user);
            responseStatus = "saved";
        }

        projectPostingRepo.save(project);
        return ResponseEntity.ok(responseStatus);
    }

}