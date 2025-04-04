package org.example.nativespark.repositories;

import org.example.nativespark.entities.CustomProductRequest;
import org.example.nativespark.entities.EntrepreneurUser;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomProductRequestRepository extends JpaRepository<CustomProductRequest, Long> {
    List<CustomProductRequest> findByUser(User user);
    List<CustomProductRequest> findAllByProduct_Entrepreneur(EntrepreneurUser entrepreneur);
}

