package org.example.nativespark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)  // Links to BusinessUser
    private BusinessUser business;

    private String projectTitle;
    private String projectDescription;
    private String projectScope;
    private Double budget;
    private LocalDateTime deadline;
    private String requiredSkills;
    private LocalDateTime postedDate;

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }
}
