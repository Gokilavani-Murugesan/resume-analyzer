package com.resumeiq.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String filename;
    private Long fileSize;
    private String jobRole;
    private String jobCompany;
    private String experienceLevel;
    private Integer atsScore;
    private String grade;
    private Integer jobMatchPercentage;

    @Column(columnDefinition = "TEXT")  private String summary;
    @Column(columnDefinition = "TEXT")  private String keywordsFound;
    @Column(columnDefinition = "TEXT")  private String keywordsMissing;
    @Column(columnDefinition = "TEXT")  private String sectionScores;
    @Column(columnDefinition = "TEXT")  private String sectionNotes;
    @Column(columnDefinition = "LONGTEXT") private String tips;
    @Column(columnDefinition = "TEXT")  private String strengths;
    @Column(columnDefinition = "TEXT")  private String quickWins;
    @Column(columnDefinition = "LONGTEXT") private String fullResult;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ResumeAnalysis() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ResumeAnalysis o = new ResumeAnalysis();
        public Builder filename(String v)           { o.filename = v; return this; }
        public Builder fileSize(Long v)             { o.fileSize = v; return this; }
        public Builder jobRole(String v)            { o.jobRole = v; return this; }
        public Builder jobCompany(String v)         { o.jobCompany = v; return this; }
        public Builder experienceLevel(String v)    { o.experienceLevel = v; return this; }
        public Builder atsScore(Integer v)          { o.atsScore = v; return this; }
        public Builder grade(String v)              { o.grade = v; return this; }
        public Builder jobMatchPercentage(Integer v){ o.jobMatchPercentage = v; return this; }
        public Builder summary(String v)            { o.summary = v; return this; }
        public Builder keywordsFound(String v)      { o.keywordsFound = v; return this; }
        public Builder keywordsMissing(String v)    { o.keywordsMissing = v; return this; }
        public Builder sectionScores(String v)      { o.sectionScores = v; return this; }
        public Builder sectionNotes(String v)       { o.sectionNotes = v; return this; }
        public Builder tips(String v)               { o.tips = v; return this; }
        public Builder strengths(String v)          { o.strengths = v; return this; }
        public Builder quickWins(String v)          { o.quickWins = v; return this; }
        public Builder fullResult(String v)         { o.fullResult = v; return this; }
        public ResumeAnalysis build()               { return o; }
    }

    public Long getId()                  { return id; }
    public String getFilename()          { return filename; }
    public Long getFileSize()            { return fileSize; }
    public String getJobRole()           { return jobRole; }
    public String getJobCompany()        { return jobCompany; }
    public String getExperienceLevel()   { return experienceLevel; }
    public Integer getAtsScore()         { return atsScore; }
    public String getGrade()             { return grade; }
    public Integer getJobMatchPercentage(){ return jobMatchPercentage; }
    public String getSummary()           { return summary; }
    public String getKeywordsFound()     { return keywordsFound; }
    public String getKeywordsMissing()   { return keywordsMissing; }
    public String getSectionScores()     { return sectionScores; }
    public String getSectionNotes()      { return sectionNotes; }
    public String getTips()              { return tips; }
    public String getStrengths()         { return strengths; }
    public String getQuickWins()         { return quickWins; }
    public String getFullResult()        { return fullResult; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    public void setId(Long v)                  { this.id = v; }
    public void setFilename(String v)          { this.filename = v; }
    public void setFileSize(Long v)            { this.fileSize = v; }
    public void setJobRole(String v)           { this.jobRole = v; }
    public void setJobCompany(String v)        { this.jobCompany = v; }
    public void setExperienceLevel(String v)   { this.experienceLevel = v; }
    public void setAtsScore(Integer v)         { this.atsScore = v; }
    public void setGrade(String v)             { this.grade = v; }
    public void setJobMatchPercentage(Integer v){ this.jobMatchPercentage = v; }
    public void setSummary(String v)           { this.summary = v; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
    public void setFullResult(String v)        { this.fullResult = v; }
}
