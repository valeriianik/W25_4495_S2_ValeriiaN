package org.example.nativespark.repositories;

import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BusinessUserRepository extends JpaRepository<BusinessUser, Long> {
    Optional<BusinessUser> findByUser(User user);
}
