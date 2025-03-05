package org.example.nativespark.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subscriptionType;  // e.g., "Free"

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    public Subscription() {}

    public Subscription(User user, String subscriptionType) {
        this.user = user;
        this.subscriptionType = subscriptionType;
    }

    public Long getId() {
        return id;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
