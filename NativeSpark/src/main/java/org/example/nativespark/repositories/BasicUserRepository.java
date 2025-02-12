package org.example.nativespark.repositories;

import org.example.nativespark.entities.BasicUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {
}
