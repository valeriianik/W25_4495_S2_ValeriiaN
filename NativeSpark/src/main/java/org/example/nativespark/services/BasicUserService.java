package org.example.nativespark.services;

import jakarta.transaction.Transactional;
import org.example.nativespark.entities.BasicUser;
import org.example.nativespark.entities.User;
import org.example.nativespark.repositories.BasicUserRepository;
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
public class BasicUserService {

    private final BasicUserRepository basicUserRepository;

    @Autowired
    public BasicUserService(BasicUserRepository basicUserRepository) {
        this.basicUserRepository = basicUserRepository;
    }

    @Transactional
    public BasicUser registerBasicUser(User user, String firstName, String lastName, String about, MultipartFile photo) throws IOException {
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

        BasicUser basicUser = new BasicUser(user, firstName, lastName, about, filePath.toString());
        return basicUserRepository.save(basicUser);
    }

    @Transactional
    public BasicUser updateBasicUser(User user, String firstName, String lastName, String about, MultipartFile photo) throws IOException {
        Optional<BasicUser> optionalBasicUser = basicUserRepository.findByUser(user);

        if (optionalBasicUser.isEmpty()) {
            throw new IllegalStateException("Basic user not found");
        }

        BasicUser basicUser = optionalBasicUser.get();
        basicUser.setFirstName(firstName);
        basicUser.setLastName(lastName);
        basicUser.setAbout(about);

        // If a new logo is uploaded, save it
        if (photo != null && !photo.isEmpty()) {
            String uploadDir = "uploads/photos/";
            File directory = new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(photo.getInputStream(), filePath);

            // Set new logo path
            basicUser.setPhotoPath(filePath.toString());
        }

        return basicUserRepository.save(basicUser);
    }
}
