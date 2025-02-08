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
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BusinessUserService {

//    private final BusinessUserRepository businessUserRepository;
//
//    @Autowired
//    public BusinessUserService(BusinessUserRepository businessUserRepository) {
//        this.businessUserRepository = businessUserRepository;
//    }
//
//    public BusinessUser registerBusinessUser(User user, String businessName, String description, MultipartFile logo) throws IOException {
//        // ✅ Handle file upload
//        String uploadDir = "uploads/logos/";
//        String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
//        Path filePath = Paths.get(uploadDir + fileName);
//
//        File directory = new File(uploadDir);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        logo.transferTo(new File(filePath.toString()));
//
//        // ✅ Save Business User in Database
//        BusinessUser businessUser = new BusinessUser(user, businessName, description, filePath.toString());
//        businessUser = businessUserRepository.save(businessUser);
//
//        System.out.println("✅ Business User Saved: " + businessUser);
//        return businessUser;
//    }

    private final BusinessUserRepository businessUserRepository;

    @Autowired
    public BusinessUserService(BusinessUserRepository businessUserRepository) {
        this.businessUserRepository = businessUserRepository;
    }

    @Transactional  // ✅ Ensures all database operations complete successfully
    public BusinessUser registerBusinessUser(User user, String businessName, String description, MultipartFile logo) throws IOException {
        // ✅ Handle file upload
        String uploadDir = "uploads/logos/";
        String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        logo.transferTo(filePath.toFile());

        // ✅ Save Business User in Database
        BusinessUser businessUser = new BusinessUser(user, businessName, description, filePath.toString());
        businessUser = businessUserRepository.save(businessUser);

        System.out.println("✅ Business User Saved: " + businessUser);
        return businessUser;
    }
}
