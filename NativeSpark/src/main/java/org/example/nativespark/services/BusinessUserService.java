package org.example.nativespark.services;

import jakarta.transaction.Transactional;
import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.BusinessUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class BusinessUserService {

    private final BusinessUserRepository businessUserRepository;

    @Autowired
    public BusinessUserService(BusinessUserRepository businessUserRepository) {
        this.businessUserRepository = businessUserRepository;
    }

    @Transactional
    public BusinessUser registerBusinessUser(User user, String businessName, String description, MultipartFile logo) throws IOException {
        String uploadDir = "uploads/logos/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Could not create upload directory: " + uploadDir);
            }
        }

        String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(logo.getInputStream(), filePath);

        BusinessUser businessUser = new BusinessUser(user, businessName, description, filePath.toString());
        return businessUserRepository.save(businessUser);
    }

    @Transactional
    public BusinessUser updateBusinessUser(User user, String businessName, String description, MultipartFile logo) throws IOException {
        Optional<BusinessUser> optionalBusinessUser = businessUserRepository.findByUser(user);

        if (optionalBusinessUser.isEmpty()) {
            throw new IllegalStateException("Business user not found");
        }

        BusinessUser businessUser = optionalBusinessUser.get();

        businessUser.setBusinessName(businessName);
        businessUser.setDescription(description);

        // If a new logo is uploaded, save it
        if (logo != null && !logo.isEmpty()) {
            String uploadDir = "uploads/logos/";
            File directory = new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(logo.getInputStream(), filePath);

            // Set new logo path
            businessUser.setLogoPath(filePath.toString());
        }

        return businessUserRepository.save(businessUser);
    }
}
