package org.example.nativespark.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "entrepreneur_users")
public class EntrepreneurUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "entrepreneur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)  // ✅ Correct reference
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String about;

    @Column(nullable = false)
    private String identityType;

    private String photoPath;

    public EntrepreneurUser() {}

    public EntrepreneurUser(User user, String firstName, String lastName, String about, String identityType, String photoPath) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.about = about;
        this.identityType = identityType;
        this.photoPath = photoPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
