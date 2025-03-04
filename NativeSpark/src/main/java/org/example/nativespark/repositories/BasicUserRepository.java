package org.example.nativespark.repositories;

import org.example.nativespark.entities.BasicUser;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {
    Optional<BasicUser> findByUser(User user);
}
