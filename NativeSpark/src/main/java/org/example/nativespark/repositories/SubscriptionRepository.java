package org.example.nativespark.repositories;

import org.example.nativespark.entities.Subscription;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser(User user);
}
