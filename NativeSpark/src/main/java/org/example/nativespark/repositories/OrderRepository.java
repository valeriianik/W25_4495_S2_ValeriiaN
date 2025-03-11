package org.example.nativespark.repositories;

import org.example.nativespark.entities.Order;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
