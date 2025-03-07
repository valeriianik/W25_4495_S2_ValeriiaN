package org.example.nativespark.repositories;

import org.example.nativespark.entities.Transaction;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}
