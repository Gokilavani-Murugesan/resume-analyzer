package com.resumeiq.controller;

import com.resumeiq.dto.AnalysisDTO.*;
import com.resumeiq.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);
    private final AnalysisService analysisService;

    public ResumeController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze(
            @RequestPart("file")                                      MultipartFile file,
            @RequestPart("jobRole")                                   String jobRole,
            @RequestPart(value = "jobCompany",      required = false) String jobCompany,
            @RequestPart(value = "experienceLevel", required = false) String experienceLevel,
            @RequestPart(value = "jobDescription",  required = false) String jobDescription) {

        if (file.isEmpty())
            return bad("EMPTY_FILE", "Please upload a resume file");
        if (jobRole == null || jobRole.isBlank())
            return bad("MISSING_ROLE", "Job role is required");

        String fname = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase() : "";
        if (!fname.endsWith(".pdf") && !fname.endsWith(".docx") && !fname.endsWith(".txt"))
            return bad("BAD_TYPE", "Only PDF, DOCX, and TXT files supported");

        try {
            AnalysisResponse r = analysisService.analyze(
                    file, jobRole, jobCompany, experienceLevel, jobDescription);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return bad("PARSE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("Analysis error: {}", e.getMessage());
            return err(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryItem>> history() {
        return ResponseEntity.ok(analysisService.history());
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> historyById(@PathVariable Long id) {
        try { return ResponseEntity.ok(analysisService.getById(id)); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long id) {
        analysisService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearAll() {
        analysisService.clearAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> stats() {
        return ResponseEntity.ok(analysisService.stats());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "ResumeIQ - No AI"));
    }

    private ResponseEntity<ErrorResponse> bad(String code, String msg) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder().error(code).message(msg).status(400).build());
    }
    private ResponseEntity<ErrorResponse> err(String msg) {
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder().error("ERROR").message(msg).status(500).build());
    }
}
