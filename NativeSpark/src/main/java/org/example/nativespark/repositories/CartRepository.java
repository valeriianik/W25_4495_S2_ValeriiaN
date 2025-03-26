package org.example.nativespark.repositories;

import org.example.nativespark.entities.Cart;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
