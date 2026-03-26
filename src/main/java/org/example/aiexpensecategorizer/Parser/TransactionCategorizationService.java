package org.example.aiexpensecategorizer.Parser;

import org.example.aiexpensecategorizer.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionCategorizationService {
    AiCategorizationService aiCategorizationService;
    public TransactionCategorizationService(AiCategorizationService aiCategorizationService) {
        this.aiCategorizationService = aiCategorizationService;
    }

    public void categorizeTransactions(List<TransactionDTO> transactions) {
        for (TransactionDTO transaction : transactions) {
            String description = transaction.getDescription() != null
                    ? transaction.getDescription().trim()
                    : "";

            String category = categorize(description, transaction.getAmount());
//
//            if ("Other".equals(category)) {
//                try {
//                    category = aiCategorizationService.categorizeTransaction(
//                            description,
//                            transaction.getAmount()
//                    );
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    category = "Other";
//                }
//            }

            transaction.setCategory(category);
        }
    }


    private String categorize(String description, Double amount) {
        if (description == null || description.isBlank()) {
            return "Other";
        }

        String normalized = description.toLowerCase();

        // Food & Dining
        if (containsAny(normalized,
                "taco bell", "daughter thai", "aloha fresh", "dessert", "crab",
                "restaurant", "doordash", "starbucks", "bowl", "hong kong")) {

            return "Food & Dining";
        }

        // Groceries
        if (containsAny(normalized,
                "safeway", "grocery outlet", "foodsco", "marukai", "market")) {
            return "Groceries";
        }

        // Shopping
        if (containsAny(normalized,
                "amazon", "aritzia", "ross", "tiktok shop", "opticontacts", "apple.com/bill")) {
            return "Shopping";
        }

        // Transportation
        if (containsAny(normalized,
                "uber", "aircanada", "air canada")) {
            return "Transportation";
        }

        // Utilities
        if (containsAny(normalized,
                "pacific gas", "pge", "republic services", "trash")) {
            return "Bills & Utilities";
        }

        // Health / Fitness / Personal care
        if (containsAny(normalized,
                "lifetime activites", "tommy cuts", "barber", "beauty")) {
            return "Health & Personal Care";
        }

        // Transfers / payments
        if (containsAny(normalized,
                "mobile payment", "thank you", "amex send", "add money")) {
            return "Transfers & Payments";
        }

        // Fees / interest
        if (containsAny(normalized,
                "interest charge", "fee")) {
            return "Fees & Interest";
        }

        return "Other";
    }

    private boolean containsAny(String normalized, String ... values){
        if (normalized == null || normalized.isBlank()) {
            return false;
        }
        for(String value: values){
            if(normalized.contains(value)){
                return true;
            }

        }
        return false;
    }
}