package org.example.nativespark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)  // Links to BusinessUser
    private BusinessUser business;

    private String jobTitle;
    private String jobDescription;
    private String location;
    private String employmentType; // Full-time, Part-time, Contract
    private Double salary;
    private String requiredExperience;
    private String requiredSkills;
    private LocalDateTime postedDate;

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }
}
