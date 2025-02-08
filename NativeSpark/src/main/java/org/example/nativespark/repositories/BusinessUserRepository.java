package org.example.nativespark.repositories;

import org.example.nativespark.entities.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser, Long> {
}
