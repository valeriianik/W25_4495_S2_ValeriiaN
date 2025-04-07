package org.example.nativespark.repositories;

import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.JobPosting;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByBusiness(BusinessUser business);
    List<JobPosting> findAllBySavedByUsersContaining(User user);
    List<JobPosting> findByJobDescriptionContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCase(String desc, String skills);
}
