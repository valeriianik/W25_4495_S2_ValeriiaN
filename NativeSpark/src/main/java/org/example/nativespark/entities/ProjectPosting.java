package org.example.nativespark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "saved_projects",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> savedByUsers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)  // Links to BusinessUser
    private BusinessUser business;

    private String projectTitle;
    private String projectDescription;
    private String projectScope;
    private Double budget;
    private LocalDate deadline;
    private String requiredSkills;
    private LocalDateTime postedDate;

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }
}
