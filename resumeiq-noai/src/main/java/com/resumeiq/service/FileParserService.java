package com.resumeiq.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileParserService {

    private static final Logger log = LoggerFactory.getLogger(FileParserService.class);

    public String extractText(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase() : "";

        String text;
        if (name.endsWith(".pdf")) {
            text = fromPdf(file.getInputStream());
        } else if (name.endsWith(".docx")) {
            text = fromDocx(file.getInputStream());
        } else if (name.endsWith(".txt")) {
            text = new String(file.getBytes());
        } else {
            throw new IllegalArgumentException("Upload PDF, DOCX, or TXT only.");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "No text found. Make sure your PDF has selectable text.");
        }

        return text.replaceAll("\\s{3,}", "  ").trim();
    }

    private String fromPdf(InputStream is) throws IOException {
        try (PDDocument doc = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(doc);
            log.info("PDF parsed: {} chars", text.length());
            return text;
        }
    }

    private String fromDocx(InputStream is) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor ex = new XWPFWordExtractor(doc)) {
            String text = ex.getText();
            log.info("DOCX parsed: {} chars", text.length());
            return text;
        }
    }
}
