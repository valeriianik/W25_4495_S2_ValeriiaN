//package org.example.nativespark.entities;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id") // ✅ Explicitly map to "user_id"
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String email;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(name = "user_type", nullable = false)
//    private String userType;
//
//    public User() {}
//
//    public User(String email, String password, String userType) {
//        this.email = email;
//        this.password = password;
//        this.userType = userType;
//    }
//
//    // ✅ Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getUserType() {
//        return userType;
//    }
//
//    public void setUserType(String userType) {
//        this.userType = userType;
//    }
//}

package org.example.nativespark.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")  // ✅ Ensure correct column name
    private Long userId;        // ✅ Match this with referencedColumnName

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false)
    private String userType;

    public User() {}

    public User(String email, String password, String userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // ✅ Getters and Setters
    public Long getUserId() {
        return userId;  // ✅ Ensure this matches referencedColumnName in BusinessUser
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}

