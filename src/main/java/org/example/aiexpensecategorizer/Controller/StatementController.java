package org.example.aiexpensecategorizer.Controller;

import org.example.aiexpensecategorizer.Parser.TransactionCategorizationService;
import org.example.aiexpensecategorizer.Parser.TransactionParsingService;
import org.example.aiexpensecategorizer.Service.PdfExtractionService;
import org.example.aiexpensecategorizer.dto.StatementUploadResponse;
import org.example.aiexpensecategorizer.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/statements")
@CrossOrigin(origins = "http://localhost:5173")
public class StatementController {
    private PdfExtractionService pdfExtractionService;
    private TransactionParsingService transactionParsingService;
    private TransactionCategorizationService transactionCategorizationService;

    public StatementController(PdfExtractionService pdfExtractionService, TransactionParsingService transactionParsingService, TransactionCategorizationService transactionCategorizationService) {

        this.pdfExtractionService = pdfExtractionService;
        this.transactionParsingService = transactionParsingService;
        this.transactionCategorizationService = transactionCategorizationService;
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

            List<TransactionDTO> transactions = transactionParsingService.parseTransactions(extractText);
            transactionCategorizationService.categorizeTransactions(transactions);

            StatementUploadResponse statementUploadResponse = new StatementUploadResponse(file.getOriginalFilename(), extractText,transactions );

            return ResponseEntity.ok(statementUploadResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();


        }


    }
}
