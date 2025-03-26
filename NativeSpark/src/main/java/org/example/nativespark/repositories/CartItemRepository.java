package org.example.nativespark.repositories;

import org.example.nativespark.entities.Cart;
import org.example.nativespark.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
}
