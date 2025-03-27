package org.example.nativespark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    // Define the one-to-many relationship with TransactionProduct
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionProduct> transactionProducts;

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
