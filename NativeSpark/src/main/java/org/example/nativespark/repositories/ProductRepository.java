package org.example.nativespark.repositories;

import org.example.nativespark.entities.EntrepreneurUser;
import org.example.nativespark.entities.Product;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByEntrepreneur(EntrepreneurUser entrepreneur);
    List<Product> findAllBySavedByUsersContaining(User user);
    List<Product> findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(String nameKeyword, String descKeyword);
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();

}
