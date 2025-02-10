package org.example.nativespark.repositories;

import org.example.nativespark.entities.EntrepreneurUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrepreneurUserRepository extends JpaRepository<EntrepreneurUser, Long> {
}
