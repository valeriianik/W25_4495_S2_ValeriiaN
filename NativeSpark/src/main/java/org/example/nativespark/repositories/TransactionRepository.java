package org.example.nativespark.repositories;

import org.example.nativespark.entities.Transaction;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBuyer(User buyer);
    List<Transaction> findBySeller(User seller);
}
