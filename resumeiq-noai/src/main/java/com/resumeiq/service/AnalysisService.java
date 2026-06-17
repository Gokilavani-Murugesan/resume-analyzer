package com.resumeiq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeiq.dto.AnalysisDTO.*;
import com.resumeiq.model.ResumeAnalysis;
import com.resumeiq.repository.ResumeAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final FileParserService       parser;
    private final RuleBasedAnalyzerService analyzer;
    private final ResumeAnalysisRepository repo;
    private final ObjectMapper             mapper;

    public AnalysisService(FileParserService parser,
                           RuleBasedAnalyzerService analyzer,
                           ResumeAnalysisRepository repo,
                           ObjectMapper mapper) {
        this.parser   = parser;
        this.analyzer = analyzer;
        this.repo     = repo;
        this.mapper   = mapper;
    }

    public AnalysisResponse analyze(MultipartFile file, String jobRole,
                                    String jobCompany, String experienceLevel,
                                    String jobDesc) throws Exception {
        // 1. Extract text
        String text = parser.extractText(file);

        // 2. Rule-based analysis
        AnalysisResult result = analyzer.analyze(text, jobRole, jobDesc);

        // 3. Save to DB
        ResumeAnalysis entity = ResumeAnalysis.builder()
                .filename(file.getOriginalFilename())
                .fileSize(file.getSize())
                .jobRole(jobRole)
                .jobCompany(jobCompany)
                .experienceLevel(experienceLevel)
                .atsScore(result.getAtsScore())
                .grade(result.getGrade())
                .jobMatchPercentage(result.getJobMatchPercentage())
                .summary(result.getSummary())
                .keywordsFound(toJson(result.getKeywordsFound()))
                .keywordsMissing(toJson(result.getKeywordsMissing()))
                .sectionScores(toJson(result.getSectionScores()))
                .sectionNotes(toJson(result.getSectionNotes()))
                .tips(toJson(result.getTips()))
                .strengths(toJson(result.getStrengths()))
                .quickWins(toJson(result.getQuickWins()))
                .fullResult(toJson(result))
                .build();

        ResumeAnalysis saved = repo.save(entity);
        log.info("Saved id={} score={}", saved.getId(), saved.getAtsScore());

        return AnalysisResponse.builder()
                .id(saved.getId())
                .filename(saved.getFilename())
                .fileSize(saved.getFileSize())
                .jobRole(saved.getJobRole())
                .jobCompany(saved.getJobCompany())
                .experienceLevel(saved.getExperienceLevel())
                .createdAt(saved.getCreatedAt())
                .result(result)
                .build();
    }

    public AnalysisResponse getById(Long id) throws Exception {
        ResumeAnalysis e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found: " + id));
        AnalysisResult r = mapper.readValue(e.getFullResult(), AnalysisResult.class);
        return AnalysisResponse.builder()
                .id(e.getId()).filename(e.getFilename()).fileSize(e.getFileSize())
                .jobRole(e.getJobRole()).jobCompany(e.getJobCompany())
                .experienceLevel(e.getExperienceLevel()).createdAt(e.getCreatedAt())
                .result(r).build();
    }

    public List<HistoryItem> history() {
        return repo.findAllByOrderByCreatedAtDesc().stream().map(e ->
                HistoryItem.builder()
                        .id(e.getId()).filename(e.getFilename())
                        .jobRole(e.getJobRole()).jobCompany(e.getJobCompany())
                        .atsScore(e.getAtsScore()).grade(e.getGrade())
                        .jobMatchPercentage(e.getJobMatchPercentage())
                        .summary(e.getSummary()).createdAt(e.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
    }

    public DashboardStats stats() {
        long   total = repo.count();
        Double avg   = repo.avgScore();
        Integer best = repo.maxScore();
        return DashboardStats.builder()
                .totalAnalyses(total)
                .averageScore(avg  != null ? Math.round(avg * 10.0) / 10.0 : 0)
                .bestScore(best != null ? best : 0)
                .excellentCount(repo.countByGrade("Excellent"))
                .goodCount(repo.countByGrade("Good"))
                .averageCount(repo.countByGrade("Average"))
                .poorCount(repo.countByGrade("Poor"))
                .build();
    }

    public void delete(Long id) { repo.deleteById(id); }
    public void clearAll()      { repo.deleteAll(); }

    private String toJson(Object o) {
        try { return o != null ? mapper.writeValueAsString(o) : "[]"; }
        catch (Exception e) { return "[]"; }
    }
}
