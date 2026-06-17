package com.resumeiq.repository;

import com.resumeiq.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    List<ResumeAnalysis> findAllByOrderByCreatedAtDesc();

    @Query("SELECT AVG(r.atsScore) FROM ResumeAnalysis r")
    Double avgScore();

    @Query("SELECT MAX(r.atsScore) FROM ResumeAnalysis r")
    Integer maxScore();

    long countByGrade(String grade);
}
