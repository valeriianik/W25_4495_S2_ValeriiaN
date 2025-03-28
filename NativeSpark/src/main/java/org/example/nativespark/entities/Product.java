package org.example.nativespark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "saved_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> savedByUsers = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<TransactionProduct> transactionProducts;

    @ManyToOne
    @JoinColumn(name = "entrepreneur_id", nullable = false)  // Links to EntrepreneurUser
    private EntrepreneurUser entrepreneur;
    // One-to-many relationship with Recommendation
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Recommendation> recommendations;

    private String productName;
    private String productDescription;
    private Double price;
    private String category;
    private String imagePath;  // Path for storing product image
    private LocalDateTime postedDate;
    private Integer quantity;

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }

    public EntrepreneurUser getEntrepreneur() {
        return entrepreneur;
    }
    public List<TransactionProduct> getTransactionProducts() {
        return transactionProducts;
    }

    public void setTransactionProducts(List<TransactionProduct> transactionProducts) {
        this.transactionProducts = transactionProducts;
    }
}
