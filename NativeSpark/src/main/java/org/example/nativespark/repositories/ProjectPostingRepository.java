package org.example.nativespark.repositories;

import org.example.nativespark.entities.BusinessUser;
import org.example.nativespark.entities.ProjectPosting;
import org.example.nativespark.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectPostingRepository extends JpaRepository<ProjectPosting, Long> {
    List<ProjectPosting> findByBusiness(BusinessUser business);
    List<ProjectPosting> findAllBySavedByUsersContaining(User user);
    List<ProjectPosting> findByProjectDescriptionContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCase(String desc, String skills);
}
