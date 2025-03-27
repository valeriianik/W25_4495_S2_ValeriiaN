package org.example.nativespark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToMany
    @JoinTable(
            name = "transaction_product",  // Join table name
            joinColumns = @JoinColumn(name = "transaction_id"),  // Foreign key for the Transaction
            inverseJoinColumns = @JoinColumn(name = "product_id") // Foreign key for the Product
    )
    private Set<Product> products = new HashSet<>();
    private int quantity;
    private double totalPrice;

    // ✅ Split shipping address fields
    private String country;
    private String city;
    private String province;
    private String streetAddress;
    private String postalCode;

    private String phoneNumber;
    private String status;  // SUCCESS / FAILED
    private LocalDateTime transactionDate;
}
