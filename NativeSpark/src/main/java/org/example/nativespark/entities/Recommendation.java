package org.example.nativespark.entities;

import jakarta.persistence.*;

@Entity(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;  // The product being recommended for

    @ManyToOne
    @JoinColumn(name = "recommended_product_id", referencedColumnName = "id", nullable = false)
    private Product recommendedProduct;  // The product being recommended

    public Recommendation(){}

    public Product getRecommendedProduct() {
        return recommendedProduct;
    }
}
