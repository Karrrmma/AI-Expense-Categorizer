package org.example.aiexpensecategorizer.Parser;

import org.apache.logging.log4j.LogManager;
import org.example.aiexpensecategorizer.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionParsingService {
    private static final Pattern TRANSACTION_PATTERN =
            Pattern.compile("(\\d{2}/\\d{2})\\s+(.*?)\\s+(-?\\$?\\d+[\\d,]*\\.\\d{2})");
    public List<TransactionDTO> parseTransactions(String extractedText) {
        List<TransactionDTO> transactions = new ArrayList<>();

        if (extractedText == null || extractedText.isBlank()) {
            return transactions;
        }

        String[] lines = extractedText.split("\\r?\\n");

        String currentDate = null;
        StringBuilder descriptionBuilder = new StringBuilder();
        boolean inTransactionSection = false;

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            // Enter real transaction/detail sections
            if (line.startsWith("Payments Amount")
                    || line.startsWith("Detail *Indicates posting date")
                    || line.startsWith("Detail Continued *Indicates posting date")) {
                inTransactionSection = true;
                currentDate = null;
                descriptionBuilder = new StringBuilder();
                continue;
            }

            // Leave transaction section when hitting non-detail sections
            if (line.startsWith("Fees")
                    || line.startsWith("Interest Charged")
                    || line.startsWith("About Trailing Interest")
                    || line.startsWith("2026 Fees and Interest Totals Year-to-Date")
                    || line.startsWith("Interest Charge Calculation")
                    || line.startsWith("IMPORTANT NOTICES")) {
                inTransactionSection = false;
                currentDate = null;
                descriptionBuilder = new StringBuilder();
                continue;
            }

            if (!inTransactionSection) {
                continue;
            }

            // Start of a transaction
            if (line.matches("^\\d{2}/\\d{2}/\\d{2}.*")) {
                currentDate = line.substring(0, 8);
                descriptionBuilder = new StringBuilder(line.substring(8).trim());
                continue;
            }

            // Amount line ends a transaction
            if (line.matches("^\\$-?\\d+[\\d,]*\\.\\d{2}.*") || line.matches("^-?\\$\\d+[\\d,]*\\.\\d{2}.*")) {
                if (currentDate != null) {
                    try {
                        String amountText = line.replace("$", "").replace(",", "").trim().split("\\s+")[0];
                        Double amount = Double.parseDouble(amountText);

                        String description = descriptionBuilder.toString().trim();

                        if (!shouldSkipDescription(description)) {
                            transactions.add(new TransactionDTO(currentDate, description, amount));
                        }

                        currentDate = null;
                        descriptionBuilder = new StringBuilder();
                    } catch (Exception ignored) {
                    }
                }
                continue;
            }

            // Continuation line
            if (currentDate != null) {
                descriptionBuilder.append(" ").append(line);
            }
        }

        return transactions;
    }

    private boolean shouldSkipDescription(String description) {
        String lower = description.toLowerCase();

        return lower.contains("new balance")
                || lower.contains("minimum payment due")
                || lower.contains("payment due date")
                || lower.contains("total interest charged")
                || lower.contains("interest charge calculation")
                || lower.contains("account total")
                || lower.contains("previous balance")
                || lower.contains("payments/credits");
    }
}
