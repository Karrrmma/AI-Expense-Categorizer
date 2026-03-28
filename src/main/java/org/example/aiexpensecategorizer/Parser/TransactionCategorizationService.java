package org.example.aiexpensecategorizer.Parser;

import org.example.aiexpensecategorizer.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionCategorizationService {
    AiCategorizationService aiCategorizationService;

    public TransactionCategorizationService(AiCategorizationService aiCategorizationService) {
        this.aiCategorizationService = aiCategorizationService;
    }

    public void categorizeTransactions(List<TransactionDTO> transactions) {
        List<TransactionDTO> needsAi = new ArrayList<>();

        for (TransactionDTO transaction : transactions) {
            String description = transaction.getDescription();
            String category = categorizer(description);
            if("Other".equals(category)) {
                needsAi.add(transaction);
            }
            else{
                transaction.setCategory(category);
            }

        }
       if(!needsAi.isEmpty()){
           Map<Integer, String> aiResult = aiCategorizationService.categorizeTransaction(needsAi);
           for(int i = 0; i <needsAi.size(); i++){
               TransactionDTO transaction = needsAi.get(i);
               String aiCategory = aiResult.get(i);
               transaction.setCategory(aiCategory);
           }

           }




    }

    private String categorizer(String description ){


            String normalized = description.toLowerCase();


            if (containsAny(normalized, "taco bell", "daughter thai", "starbucks", "doordash", "dessert", "restaurant", "bowl")) {
                return "Food & Dining";
            }


            if (containsAny(normalized, "safeway", "grocery outlet", "foodsco", "marukai", "foodmaxx")) {
                return "Groceries";
            }

            if (containsAny(normalized, "amazon", "aritzia", "ross", "tiktok", "home depot", "apple.com/bill")) {
                return "Shopping";
            }

            if (containsAny(normalized, "uber")) {
                return "Transportation";
            }

            if (containsAny(normalized, "air canada", "aircanada")) {
                return "Travel";
            }

            if (containsAny(normalized, "pacific gas", "pge", "republic services", "trash")) {
                return "Bills & Utilities";
            }

            if (containsAny(normalized, "mobile payment", "amex send", "add money", "thank you")) {
                return "Transfers & Payments";
            }

            if (containsAny(normalized, "interest charge", "fee")) {
                return "Fees & Interest";
            }

            if (containsAny(normalized, "tommy cuts", "beauty", "barber", "lifetime activites")) {
                return "Health & Personal Care";
            }

            return "Other";




    }

    public boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword))        {
                return true;
            }

        }
        return false;


    }

}



