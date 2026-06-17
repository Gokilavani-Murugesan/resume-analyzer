package com.resumeiq.service;

import com.resumeiq.dto.AnalysisDTO.AnalysisResult;
import com.resumeiq.dto.AnalysisDTO.TipItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rule-based ATS Resume Analyzer — NO AI API required.
 * Analyzes resume text using keyword matching, section detection,
 * formatting checks, and scoring algorithms.
 */
@Service
public class RuleBasedAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(RuleBasedAnalyzerService.class);

    // Common tech keywords by category
    private static final List<String> TECH_KEYWORDS = Arrays.asList(
        "java","python","javascript","typescript","react","angular","vue","spring","spring boot",
        "node.js","nodejs","express","django","flask","html","css","sql","mysql","postgresql",
        "mongodb","redis","docker","kubernetes","aws","azure","gcp","git","github","gitlab",
        "rest api","graphql","microservices","agile","scrum","ci/cd","jenkins","maven","gradle",
        "junit","testing","linux","bash","c++","c#","php","ruby","swift","kotlin","android",
        "ios","machine learning","deep learning","tensorflow","pytorch","data science","pandas",
        "numpy","hibernate","jpa","jdbc","spring mvc","spring security","oauth","jwt",
        "html5","css3","bootstrap","tailwind","webpack","npm","yarn","figma","jira","confluence"
    );

    // Common soft skill keywords
    private static final List<String> SOFT_KEYWORDS = Arrays.asList(
        "leadership","communication","teamwork","problem solving","analytical","creative",
        "organized","detail oriented","fast learner","self motivated","collaborative",
        "adaptable","time management","critical thinking","interpersonal"
    );

    // Section headers to detect
    private static final List<String> SECTION_HEADERS = Arrays.asList(
        "experience","work experience","employment","education","skills","technical skills",
        "projects","summary","objective","certifications","achievements","contact","profile"
    );

    public AnalysisResult analyze(String resumeText, String jobRole, String jobDescription) {
        log.info("Starting rule-based analysis for role: {}", jobRole);

        String text = resumeText.toLowerCase();
        String jd   = (jobDescription != null ? jobDescription : "").toLowerCase();

        // ── Section Detection ──────────────────────────────────────
        boolean hasContact    = hasSection(text, "contact","email","phone","linkedin","address","@");
        boolean hasSummary    = hasSection(text, "summary","objective","profile","about");
        boolean hasSkills     = hasSection(text, "skills","technologies","technical","expertise");
        boolean hasExperience = hasSection(text, "experience","employment","work history","worked at","position");
        boolean hasEducation  = hasSection(text, "education","university","college","degree","bachelor","master","b.tech","b.e","m.tech");

        // ── Keyword Analysis ───────────────────────────────────────
        List<String> allKeywords = new ArrayList<>(TECH_KEYWORDS);
        allKeywords.addAll(SOFT_KEYWORDS);

        // Extract keywords from JD
        List<String> jdKeywords = new ArrayList<>();
        if (!jd.isBlank()) {
            for (String kw : allKeywords) {
                if (jd.contains(kw.toLowerCase())) {
                    jdKeywords.add(kw);
                }
            }
        } else {
            // Use common tech keywords if no JD provided
            jdKeywords = new ArrayList<>(TECH_KEYWORDS.subList(0, Math.min(20, TECH_KEYWORDS.size())));
        }

        List<String> found   = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String kw : jdKeywords) {
            if (text.contains(kw.toLowerCase())) {
                found.add(kw);
            } else {
                missing.add(kw);
            }
        }

        // Also find extra tech keywords present in resume
        for (String kw : TECH_KEYWORDS) {
            if (text.contains(kw.toLowerCase()) && !found.contains(kw)) {
                found.add(kw);
            }
        }

        // ── Section Scores ─────────────────────────────────────────
        int contactScore    = scoreSection(text, hasContact,
                Arrays.asList("email","phone","linkedin","github","address","@","+91","+1"), 60);
        int summaryScore    = scoreSection(text, hasSummary,
                Arrays.asList("summary","objective","experienced","professional","developer","engineer"), 50);
        int skillsScore     = scoreSection(text, hasSkills,
                Arrays.asList("java","python","sql","html","css","javascript","spring","react","angular"), 50);
        int experienceScore = scoreSection(text, hasExperience,
                Arrays.asList("years","experience","developed","built","implemented","managed","led","designed"), 55);
        int educationScore  = scoreSection(text, hasEducation,
                Arrays.asList("degree","university","college","gpa","cgpa","bachelor","master","b.tech","b.e"), 55);

        // ── ATS Score Calculation ──────────────────────────────────
        int sectionScore  = (contactScore + summaryScore + skillsScore + experienceScore + educationScore) / 5;
        int keywordScore  = jdKeywords.isEmpty() ? 60
                : (int)((found.size() * 100.0) / Math.max(jdKeywords.size(), 1));
        int lengthScore   = scoreLengthAndFormat(resumeText);
        int formattingScore = scoreFormatting(resumeText);

        int atsScore = (int)(
                sectionScore  * 0.30 +
                keywordScore  * 0.35 +
                lengthScore   * 0.20 +
                formattingScore * 0.15
        );
        atsScore = Math.min(98, Math.max(20, atsScore));

        int jobMatch = jdKeywords.isEmpty() ? atsScore
                : Math.min(98, (int)((found.size() * 100.0) / Math.max(jdKeywords.size(), 1)));

        String grade = atsScore >= 80 ? "Excellent"
                     : atsScore >= 65 ? "Good"
                     : atsScore >= 45 ? "Average"
                     : "Poor";

        // ── Summary ────────────────────────────────────────────────
        String summary = buildSummary(atsScore, grade, found.size(), missing.size(),
                hasContact, hasSummary, hasSkills, hasExperience, hasEducation);

        // ── Section Score Map ──────────────────────────────────────
        Map<String, Integer> sectionScores = new LinkedHashMap<>();
        sectionScores.put("contact",         contactScore);
        sectionScores.put("summary_section", summaryScore);
        sectionScores.put("skills",          skillsScore);
        sectionScores.put("experience",      experienceScore);
        sectionScores.put("education",       educationScore);

        Map<String, String> sectionNotes = new LinkedHashMap<>();
        sectionNotes.put("contact",         hasContact    ? "Contact details found — good visibility." : "Add email, phone, and LinkedIn URL.");
        sectionNotes.put("summary_section", hasSummary    ? "Summary section detected." : "Add a 2-3 line professional summary at the top.");
        sectionNotes.put("skills",          hasSkills     ? "Skills section found with technical keywords." : "Add a dedicated Skills section.");
        sectionNotes.put("experience",      hasExperience ? "Work experience section detected." : "Add your work experience with clear job titles and dates.");
        sectionNotes.put("education",       hasEducation  ? "Education section found." : "Add your educational qualifications.");

        // ── Strengths ──────────────────────────────────────────────
        List<String> strengths = buildStrengths(found, hasContact, hasSummary,
                hasSkills, hasExperience, hasEducation, resumeText);

        // ── Quick Wins ─────────────────────────────────────────────
        List<String> quickWins = buildQuickWins(hasContact, hasSummary,
                hasSkills, hasExperience, hasEducation, missing, resumeText);

        // ── Tips ───────────────────────────────────────────────────
        Map<String, List<TipItem>> tips = buildTips(hasContact, hasSummary,
                hasSkills, hasExperience, hasEducation, missing, resumeText, found);

        // ── Build Result ───────────────────────────────────────────
        AnalysisResult result = new AnalysisResult();
        result.setAtsScore(atsScore);
        result.setGrade(grade);
        result.setSummary(summary);
        result.setJobMatchPercentage(jobMatch);
        result.setSectionScores(sectionScores);
        result.setSectionNotes(sectionNotes);
        result.setKeywordsFound(found.stream().limit(20).collect(Collectors.toList()));
        result.setKeywordsMissing(missing.stream().limit(15).collect(Collectors.toList()));
        result.setStrengths(strengths);
        result.setQuickWins(quickWins);
        result.setTips(tips);

        log.info("Analysis done. Score={} Grade={} Found={} Missing={}",
                atsScore, grade, found.size(), missing.size());
        return result;
    }

    // ── Helpers ────────────────────────────────────────────────────

    private boolean hasSection(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw.toLowerCase())) return true;
        }
        return false;
    }

    private int scoreSection(String text, boolean exists, List<String> indicators, int base) {
        if (!exists) return base - 30;
        int score = base;
        for (String ind : indicators) {
            if (text.contains(ind.toLowerCase())) score += 5;
        }
        return Math.min(100, score);
    }

    private int scoreLengthAndFormat(String text) {
        int len = text.length();
        if (len < 300)  return 30;
        if (len < 600)  return 50;
        if (len < 1500) return 75;
        if (len < 4000) return 90;
        return 70; // too long
    }

    private int scoreFormatting(String text) {
        int score = 60;
        // Check for bullet points or structured content
        if (text.contains("•") || text.contains("-") || text.contains("*")) score += 10;
        // Check for dates (year patterns)
        if (text.matches("(?s).*\\b(19|20)\\d{2}\\b.*")) score += 10;
        // Check for proper line breaks
        if (text.contains("\n")) score += 10;
        // Penalize if too many special characters (tables, graphics)
        long specialChars = text.chars().filter(c -> c == '|' || c == '+').count();
        if (specialChars > 20) score -= 15;
        return Math.min(100, score);
    }

    private String buildSummary(int score, String grade, int found, int missing,
            boolean contact, boolean summary, boolean skills, boolean exp, boolean edu) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your resume has an ATS score of ").append(score).append("/100 (").append(grade).append("). ");
        if (score >= 80) {
            sb.append("Great job — your resume is well-optimized for ATS systems with strong keyword coverage and clear structure.");
        } else if (score >= 65) {
            sb.append("Your resume is decent but can be improved by adding missing keywords and strengthening weak sections.");
        } else if (score >= 45) {
            sb.append("Your resume needs improvement. Focus on adding missing keywords and ensuring all key sections are present.");
        } else {
            sb.append("Your resume needs significant work. Add all missing sections, keywords, and improve the overall structure.");
        }
        return sb.toString();
    }

    private List<String> buildStrengths(List<String> found, boolean contact, boolean summary,
            boolean skills, boolean exp, boolean edu, String text) {
        List<String> strengths = new ArrayList<>();
        if (contact)    strengths.add("Contact information is clearly present and accessible.");
        if (exp)        strengths.add("Work experience section is well-structured.");
        if (edu)        strengths.add("Educational qualifications are clearly mentioned.");
        if (skills)     strengths.add("Dedicated skills section improves ATS keyword matching.");
        if (summary)    strengths.add("Professional summary gives recruiters a quick overview.");
        if (found.size() > 10) strengths.add("Good number of relevant keywords found: " + found.size() + " matches.");
        if (text.length() > 1000) strengths.add("Resume has adequate detail and content length.");
        if (strengths.isEmpty()) strengths.add("Resume has basic structure in place.");
        return strengths.stream().limit(5).collect(Collectors.toList());
    }

    private List<String> buildQuickWins(boolean contact, boolean summary, boolean skills,
            boolean exp, boolean edu, List<String> missing, String text) {
        List<String> wins = new ArrayList<>();
        if (!summary)  wins.add("Add a 2-3 line professional summary at the top of your resume.");
        if (!skills)   wins.add("Add a dedicated Technical Skills section with your technologies.");
        if (!contact)  wins.add("Add your email, phone number, and LinkedIn URL.");
        if (!exp)      wins.add("Add your work experience with company names, roles, and dates.");
        if (!edu)      wins.add("Add your educational qualifications with degree and institution.");
        if (!missing.isEmpty()) {
            wins.add("Add these missing keywords: " +
                    missing.stream().limit(5).collect(Collectors.joining(", ")) + ".");
        }
        if (text.length() < 600) wins.add("Expand your resume with more details and achievements.");
        wins.add("Quantify your achievements with numbers (e.g. 'Improved performance by 30%').");
        wins.add("Use action verbs: Developed, Built, Implemented, Designed, Led, Managed.");
        return wins.stream().limit(5).collect(Collectors.toList());
    }

    private Map<String, List<TipItem>> buildTips(boolean contact, boolean summary,
            boolean skills, boolean exp, boolean edu,
            List<String> missing, String text, List<String> found) {

        Map<String, List<TipItem>> tips = new LinkedHashMap<>();

        // Formatting tips
        List<TipItem> formatting = new ArrayList<>();
        formatting.add(new TipItem(
            "Use a single-column layout",
            "ATS systems struggle with multi-column formats, tables, and text boxes. Use a simple single-column format.",
            "High",
            "Two-column layout with sidebar for skills",
            "Single column with Skills section listed vertically"
        ));
        formatting.add(new TipItem(
            "Use standard section headers",
            "ATS systems look for standard headers like 'Experience', 'Education', 'Skills'. Avoid creative names.",
            "High",
            "My Journey, What I Know, Where I Studied",
            "Work Experience, Technical Skills, Education"
        ));
        formatting.add(new TipItem(
            "Use bullet points for experience",
            "Use bullet points to list your responsibilities and achievements. Makes it easy for ATS to parse.",
            "Medium",
            "Paragraph describing all job duties in a block of text",
            "• Developed REST APIs using Spring Boot\n• Reduced load time by 40%"
        ));
        if (text.length() < 600) {
            formatting.add(new TipItem(
                "Increase resume length",
                "Your resume seems too short. A good resume should be 1-2 pages with adequate detail.",
                "High",
                "Short 200-word resume missing key details",
                "Detailed 600-1000 word resume with all sections"
            ));
        }
        tips.put("formatting", formatting);

        // Keyword tips
        List<TipItem> keywords = new ArrayList<>();
        if (!missing.isEmpty()) {
            keywords.add(new TipItem(
                "Add missing keywords from job description",
                "Your resume is missing important keywords. Add them naturally in your skills and experience.",
                "High",
                "Resume without: " + missing.stream().limit(3).collect(Collectors.joining(", ")),
                "Resume includes: " + missing.stream().limit(3).collect(Collectors.joining(", "))
            ));
        }
        keywords.add(new TipItem(
            "Mirror the job description language",
            "Use the same words and phrases from the job description in your resume.",
            "High",
            "Used 'made websites' when JD says 'developed web applications'",
            "Used 'developed web applications' matching the JD exactly"
        ));
        keywords.add(new TipItem(
            "Include both acronyms and full forms",
            "Write both the short form and full form of technologies (e.g. 'SQL (Structured Query Language)').",
            "Medium",
            "Only wrote 'ML' without full form",
            "Wrote 'Machine Learning (ML)' covering both variations"
        ));
        tips.put("keywords", keywords);

        // Content tips
        List<TipItem> content = new ArrayList<>();
        content.add(new TipItem(
            "Quantify your achievements",
            "Add numbers and metrics to show impact. ATS and recruiters love measurable results.",
            "High",
            "Improved application performance",
            "Improved application performance by 45%, reducing load time from 8s to 4.4s"
        ));
        content.add(new TipItem(
            "Use strong action verbs",
            "Start each bullet point with a strong action verb to make your experience stand out.",
            "Medium",
            "Was responsible for managing the database",
            "Managed and optimized MySQL database serving 50,000+ daily users"
        ));
        if (!summary) {
            content.add(new TipItem(
                "Add a professional summary",
                "A 2-3 line summary at the top gives recruiters a quick overview and improves ATS scoring.",
                "High",
                "Resume starts directly with education or experience",
                "Starts with: 'Full-stack developer with 3 years of experience in Java and React...'"
            ));
        }
        tips.put("content", content);

        // Structure tips
        List<TipItem> structure = new ArrayList<>();
        structure.add(new TipItem(
            "Follow the correct section order",
            "ATS systems expect a standard order: Contact → Summary → Skills → Experience → Education.",
            "High",
            "Education listed before Experience and Skills",
            "Contact Info → Summary → Skills → Work Experience → Education"
        ));
        if (!skills) {
            structure.add(new TipItem(
                "Add a dedicated Skills section",
                "A separate Skills section helps ATS quickly identify your technical competencies.",
                "High",
                "Skills scattered throughout the resume in paragraphs",
                "Technical Skills: Java, Spring Boot, MySQL, React, Docker, AWS"
            ));
        }
        structure.add(new TipItem(
            "Add dates to all experiences",
            "Include start and end dates (Month Year) for all work experiences and education.",
            "Medium",
            "Software Engineer at ABC Company (no dates)",
            "Software Engineer at ABC Company (Jan 2022 – Present)"
        ));
        tips.put("structure", structure);

        return tips;
    }
}
