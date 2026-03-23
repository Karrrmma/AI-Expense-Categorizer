package org.example.aiexpensecategorizer.Controller;

import org.example.aiexpensecategorizer.Service.PdfExtractionService;
import org.example.aiexpensecategorizer.dto.StatementUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/statements")
@CrossOrigin(origins = "http://localhost:5173")
public class StatementController {
    private PdfExtractionService pdfExtractionService;

    public StatementController(PdfExtractionService pdfExtractionService) {
        this.pdfExtractionService = pdfExtractionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<StatementUploadResponse> upload(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String extractText = pdfExtractionService.extractPdf(file);
            StatementUploadResponse statementUploadResponse = new StatementUploadResponse(file.getOriginalFilename(), extractText);

            return ResponseEntity.ok(statementUploadResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();


        }


    }
}
