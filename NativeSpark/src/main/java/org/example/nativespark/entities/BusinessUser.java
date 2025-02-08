//package org.example.nativespark.entities;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "business_users")
//public class BusinessUser {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true) // ✅ Correct reference
//    private User user;
//
//
//    @Column(nullable = false)
//    private String businessName;
//
//    @Column(nullable = false)
//    private String description;
//
//    private String logoPath; // ✅ Field to store logo path
//
//    public BusinessUser() {}
//
//    public BusinessUser(User user, String businessName, String description, String logoPath) {
//        this.user = user;
//        this.businessName = businessName;
//        this.description = description;
//        this.logoPath = logoPath;
//    }
//
//    // ✅ Getters and setters
//    public Long getId() {
//        return id;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public String getBusinessName() {
//        return businessName;
//    }
//
//    public void setBusinessName(String businessName) {
//        this.businessName = businessName;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getLogoPath() {
//        return logoPath;
//    }
//
//    public void setLogoPath(String logoPath) {
//        this.logoPath = logoPath;
//    }
//}

package org.example.nativespark.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "business_users")
public class BusinessUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)  // ✅ Matches User entity
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String description;

    private String logoPath; // ✅ Field to store logo path

    public BusinessUser() {}

    public BusinessUser(User user, String businessName, String description, String logoPath) {
        this.user = user;
        this.businessName = businessName;
        this.description = description;
        this.logoPath = logoPath;
    }

    // ✅ Getters and setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}

