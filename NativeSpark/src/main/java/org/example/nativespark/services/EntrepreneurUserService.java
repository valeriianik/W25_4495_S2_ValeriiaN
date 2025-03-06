package org.example.nativespark.services;

import jakarta.transaction.Transactional;
import org.example.nativespark.entities.EntrepreneurUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.EntrepreneurUserRepository;
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
public class EntrepreneurUserService {

    private final EntrepreneurUserRepository entrepreneurUserRepository;

    @Autowired
    public EntrepreneurUserService(EntrepreneurUserRepository entrepreneurUserRepository) {
        this.entrepreneurUserRepository = entrepreneurUserRepository;
    }

    @Transactional
    public EntrepreneurUser registerEntrepreneurUser(User user, String firstName, String lastName, String about, String identityType, MultipartFile photo) throws IOException {
        String uploadDir = "uploads/photos/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Could not create upload directory: " + uploadDir);
            }
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(photo.getInputStream(), filePath);

        EntrepreneurUser entrepreneurUser = new EntrepreneurUser(user, firstName, lastName, about, identityType, filePath.toString());
        return entrepreneurUserRepository.save(entrepreneurUser);
    }

    @Transactional
    public EntrepreneurUser updateEntrepreneurUser(User user, String firstName, String lastName, String identityType, String about) {
        Optional<EntrepreneurUser> optionalEntrepreneurUser = entrepreneurUserRepository.findByUser(user);

        if (optionalEntrepreneurUser.isEmpty()) {
            throw new IllegalStateException("Entrepreneur user not found");
        }

        EntrepreneurUser entrepreneurUser = optionalEntrepreneurUser.get();
        entrepreneurUser.setFirstName(firstName);
        entrepreneurUser.setLastName(lastName);
        entrepreneurUser.setIdentityType(identityType);
        entrepreneurUser.setAbout(about);

        return entrepreneurUserRepository.save(entrepreneurUser);
    }
}

