package com.resumeiq.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalysisDTO {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalysisResult {
        @JsonProperty("ats_score")           private Integer atsScore;
        private String grade;
        private String summary;
        @JsonProperty("job_match_percentage") private Integer jobMatchPercentage;
        @JsonProperty("section_scores")       private Map<String, Integer> sectionScores;
        @JsonProperty("section_notes")        private Map<String, String>  sectionNotes;
        @JsonProperty("keywords_found")       private List<String> keywordsFound;
        @JsonProperty("keywords_missing")     private List<String> keywordsMissing;
        private List<String> strengths;
        @JsonProperty("quick_wins")           private List<String> quickWins;
        private Map<String, List<TipItem>>    tips;

        public AnalysisResult() {}

        public Integer getAtsScore()                              { return atsScore; }
        public void setAtsScore(Integer v)                        { this.atsScore = v; }
        public String getGrade()                                  { return grade; }
        public void setGrade(String v)                            { this.grade = v; }
        public String getSummary()                                { return summary; }
        public void setSummary(String v)                          { this.summary = v; }
        public Integer getJobMatchPercentage()                    { return jobMatchPercentage; }
        public void setJobMatchPercentage(Integer v)              { this.jobMatchPercentage = v; }
        public Map<String, Integer> getSectionScores()            { return sectionScores; }
        public void setSectionScores(Map<String, Integer> v)      { this.sectionScores = v; }
        public Map<String, String> getSectionNotes()              { return sectionNotes; }
        public void setSectionNotes(Map<String, String> v)        { this.sectionNotes = v; }
        public List<String> getKeywordsFound()                    { return keywordsFound; }
        public void setKeywordsFound(List<String> v)              { this.keywordsFound = v; }
        public List<String> getKeywordsMissing()                  { return keywordsMissing; }
        public void setKeywordsMissing(List<String> v)            { this.keywordsMissing = v; }
        public List<String> getStrengths()                        { return strengths; }
        public void setStrengths(List<String> v)                  { this.strengths = v; }
        public List<String> getQuickWins()                        { return quickWins; }
        public void setQuickWins(List<String> v)                  { this.quickWins = v; }
        public Map<String, List<TipItem>> getTips()               { return tips; }
        public void setTips(Map<String, List<TipItem>> v)         { this.tips = v; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TipItem {
        private String title;
        private String description;
        private String priority;
        private String before;
        private String after;

        public TipItem() {}
        public TipItem(String title, String description, String priority, String before, String after) {
            this.title = title; this.description = description;
            this.priority = priority; this.before = before; this.after = after;
        }

        public String getTitle()                { return title; }
        public void setTitle(String v)          { this.title = v; }
        public String getDescription()          { return description; }
        public void setDescription(String v)    { this.description = v; }
        public String getPriority()             { return priority; }
        public void setPriority(String v)       { this.priority = v; }
        public String getBefore()               { return before; }
        public void setBefore(String v)         { this.before = v; }
        public String getAfter()                { return after; }
        public void setAfter(String v)          { this.after = v; }
    }

    public static class AnalysisResponse {
        private Long id;
        private String filename;
        private Long fileSize;
        private String jobRole;
        private String jobCompany;
        private String experienceLevel;
        private LocalDateTime createdAt;
        private AnalysisResult result;

        public AnalysisResponse() {}
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final AnalysisResponse o = new AnalysisResponse();
            public Builder id(Long v)                { o.id = v; return this; }
            public Builder filename(String v)        { o.filename = v; return this; }
            public Builder fileSize(Long v)          { o.fileSize = v; return this; }
            public Builder jobRole(String v)         { o.jobRole = v; return this; }
            public Builder jobCompany(String v)      { o.jobCompany = v; return this; }
            public Builder experienceLevel(String v) { o.experienceLevel = v; return this; }
            public Builder createdAt(LocalDateTime v){ o.createdAt = v; return this; }
            public Builder result(AnalysisResult v)  { o.result = v; return this; }
            public AnalysisResponse build()          { return o; }
        }
        public Long getId()                  { return id; }
        public String getFilename()          { return filename; }
        public Long getFileSize()            { return fileSize; }
        public String getJobRole()           { return jobRole; }
        public String getJobCompany()        { return jobCompany; }
        public String getExperienceLevel()   { return experienceLevel; }
        public LocalDateTime getCreatedAt()  { return createdAt; }
        public AnalysisResult getResult()    { return result; }
    }

    public static class HistoryItem {
        private Long id;
        private String filename;
        private String jobRole;
        private String jobCompany;
        private Integer atsScore;
        private String grade;
        private Integer jobMatchPercentage;
        private String summary;
        private LocalDateTime createdAt;

        public HistoryItem() {}
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final HistoryItem o = new HistoryItem();
            public Builder id(Long v)                   { o.id = v; return this; }
            public Builder filename(String v)           { o.filename = v; return this; }
            public Builder jobRole(String v)            { o.jobRole = v; return this; }
            public Builder jobCompany(String v)         { o.jobCompany = v; return this; }
            public Builder atsScore(Integer v)          { o.atsScore = v; return this; }
            public Builder grade(String v)              { o.grade = v; return this; }
            public Builder jobMatchPercentage(Integer v){ o.jobMatchPercentage = v; return this; }
            public Builder summary(String v)            { o.summary = v; return this; }
            public Builder createdAt(LocalDateTime v)   { o.createdAt = v; return this; }
            public HistoryItem build()                  { return o; }
        }
        public Long getId()                 { return id; }
        public String getFilename()         { return filename; }
        public String getJobRole()          { return jobRole; }
        public String getJobCompany()       { return jobCompany; }
        public Integer getAtsScore()        { return atsScore; }
        public String getGrade()            { return grade; }
        public Integer getJobMatchPercentage(){ return jobMatchPercentage; }
        public String getSummary()          { return summary; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class DashboardStats {
        private long   totalAnalyses;
        private double averageScore;
        private int    bestScore;
        private long   excellentCount;
        private long   goodCount;
        private long   averageCount;
        private long   poorCount;

        public DashboardStats() {}
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final DashboardStats o = new DashboardStats();
            public Builder totalAnalyses(long v)   { o.totalAnalyses = v; return this; }
            public Builder averageScore(double v)  { o.averageScore = v; return this; }
            public Builder bestScore(int v)        { o.bestScore = v; return this; }
            public Builder excellentCount(long v)  { o.excellentCount = v; return this; }
            public Builder goodCount(long v)       { o.goodCount = v; return this; }
            public Builder averageCount(long v)    { o.averageCount = v; return this; }
            public Builder poorCount(long v)       { o.poorCount = v; return this; }
            public DashboardStats build()          { return o; }
        }
        public long   getTotalAnalyses()  { return totalAnalyses; }
        public double getAverageScore()   { return averageScore; }
        public int    getBestScore()      { return bestScore; }
        public long   getExcellentCount() { return excellentCount; }
        public long   getGoodCount()      { return goodCount; }
        public long   getAverageCount()   { return averageCount; }
        public long   getPoorCount()      { return poorCount; }
    }

    public static class ErrorResponse {
        private String error;
        private String message;
        private int    status;

        public ErrorResponse() {}
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private final ErrorResponse o = new ErrorResponse();
            public Builder error(String v)   { o.error = v; return this; }
            public Builder message(String v) { o.message = v; return this; }
            public Builder status(int v)     { o.status = v; return this; }
            public ErrorResponse build()     { return o; }
        }
        public String getError()   { return error; }
        public String getMessage() { return message; }
        public int    getStatus()  { return status; }
    }
}
