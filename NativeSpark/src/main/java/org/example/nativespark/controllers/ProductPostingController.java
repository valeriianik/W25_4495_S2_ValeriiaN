package org.example.nativespark.controllers;

import org.example.nativespark.entities.*;
import org.example.nativespark.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/product-postings")
public class ProductPostingController {

    private final ProductRepository productRepository;
    private final EntrepreneurUserRepository entrepreneurUserRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads/products/";  // ✅ Store in products folder

    public ProductPostingController(ProductRepository productRepository, EntrepreneurUserRepository entrepreneurUserRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.entrepreneurUserRepository = entrepreneurUserRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public String showProductPostingForm(Model model, Principal principal) {
        User user = getAuthenticatedUser(principal);
        if (user == null) return "redirect:/login";

        Optional<EntrepreneurUser> entrepreneurUser = entrepreneurUserRepository.findByUser(user);
        if (entrepreneurUser.isEmpty()) {
            return "redirect:/account"; // Redirect if the user is not an entrepreneur
        }

        model.addAttribute("product", new Product());
        return "product-posting-form"; // Thymeleaf template for adding a product
    }

    @PostMapping("/save")
    public String saveProductPosting(@ModelAttribute Product product,
                                     @RequestParam("imageFile") MultipartFile imageFile,
                                     Principal principal) throws IOException {
        User user = getAuthenticatedUser(principal);
        if (user == null) return "redirect:/login";

        Optional<EntrepreneurUser> entrepreneurUser = entrepreneurUserRepository.findByUser(user);
        if (entrepreneurUser.isPresent()) {
            product.setEntrepreneur(entrepreneurUser.get());
            product.setPostedDate(LocalDateTime.now());

            // ✅ Handle Image Upload
            if (!imageFile.isEmpty()) {
                File directory = new File(UPLOAD_DIR);
                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (!created) {
                        throw new IOException("Could not create upload directory: " + UPLOAD_DIR);
                    }
                }

                // ✅ Generate unique filename
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);

                Files.copy(imageFile.getInputStream(), filePath);

                // ✅ Save relative image path for Thymeleaf
                product.setImagePath("/" + UPLOAD_DIR + fileName);
            }

            productRepository.save(product);
        }

        return "redirect:/my_postings"; // ✅ Redirect back to My Postings page
    }

    // ✅ Utility Method: Get authenticated User
    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) return null;
        String email = principal.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    @PostMapping("/delete/{id}")
    public String deleteProductPosting(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/my_postings";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            return "redirect:/my_postings?error=ProductNotFound";  // Redirect if not found
        }

        model.addAttribute("product", product.get());
        return "edit-product";
    }

    @PostMapping("/update/{id}")
    public String updateProductPosting(@PathVariable Long id, @ModelAttribute Product product,
                                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        Optional<Product> existingProduct = productRepository.findById(id);

        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setProductName(product.getProductName());
            updatedProduct.setCategory(product.getCategory());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setProductDescription(product.getProductDescription());
            updatedProduct.setQuantity(product.getQuantity());

            // ✅ Handle Image Upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get("uploads/products/", fileName);
                Files.copy(imageFile.getInputStream(), filePath);
                updatedProduct.setImagePath("/uploads/products/" + fileName);
            }

            productRepository.save(updatedProduct);
        }

        return "redirect:/my_postings";
    }

    @GetMapping("/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // Redirect to login page if the user is not authenticated
        }

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return "redirect:/?error=ProductNotFound"; // Redirect to home page if the product is not found
        }

        model.addAttribute("product", product.get());
        return "product-details"; // Load the product details Thymeleaf template
    }
}

