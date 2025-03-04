package org.example.nativespark.repositories;

import org.example.nativespark.entities.EntrepreneurUser;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepreneurUserRepository extends JpaRepository<EntrepreneurUser, Long> {
    Optional<EntrepreneurUser> findByUser(User user);
}

