package org.example.aiexpensecategorizer.Parser;


import org.example.aiexpensecategorizer.dto.TransactionDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;


@Service
public class AiCategorizationService {

    private final WebClient webClient;

    public AiCategorizationService(@Value("${openai.api.key}") String apiKey) {
        System.out.println("Loaded OpenAI key? " + (apiKey != null && !apiKey.isBlank()));

        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Map<Integer, String> categorizeTransaction(List<TransactionDTO> transactions) {
        try {
            if (transactions == null || transactions.isEmpty()){
                return Collections.emptyMap();
            }
            StringBuilder batchInput = new StringBuilder();
            for (int i = 0; i<transactions.size(); i++){
                TransactionDTO txn = transactions.get(i);
                batchInput.append(i).append(" | Description").append(txn.getDescription()) .append(txn.getDescription())
                        .append(" | Amount: ")
                        .append(txn.getAmount())
                        .append("\n");

            }


            String prompt = """
                    You are a financial transaction classifier.

                    Choose exactly one category for each transaction from this list:
                    Food & Dining, Groceries, Shopping, Transportation, Bills & Utilities,
                    Entertainment, Health & Personal Care, Travel, Transfers & Payments,
                    Fees & Interest, Other.

                    Return ONLY valid JSON as an array of objects in this format:
                    [
                      {"index": 0, "category": "Shopping"},
                      {"index": 1, "category": "Food & Dining"}
                    ]

                    Transactions:
                    %s
                    """.formatted(batchInput);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            Map<String, Object> response= webClient.post().uri("/chat/completions").bodyValue(requestBody).retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                          return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("API Error: " + errorBody)));
            }).bodyToMono(Map.class).block();

            System.out.println("OpenAI Batch raw response: " + response);

            List<Map<String, Object>> choices= (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            return parseBatchResponse(content);

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
    private Map<Integer, String> parseBatchResponse(String jsonText) {
        Map<Integer, String> results = new HashMap<>();

        if (jsonText == null || jsonText.isBlank()) {
            return results;
        }

        Pattern pattern = Pattern.compile("\\{\\s*\"index\"\\s*:\\s*(\\d+)\\s*,\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*}");
        Matcher matcher = pattern.matcher(jsonText);

        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String category = sanitizeCategory(matcher.group(2));
            results.put(index, category);
        }

        return results;
    }

    private String sanitizeCategory(String category) {
        if (category == null) {
            return "Other";
        }

        String cleaned = category.replace("\n", "").trim();

        String[] allowed = {
                "Food & Dining",
                "Groceries",
                "Shopping",
                "Transportation",
                "Bills & Utilities",
                "Entertainment",
                "Health & Personal Care",
                "Travel",
                "Transfers & Payments",
                "Fees & Interest",
                "Other"
        };

        for (String valid : allowed) {
            if (valid.equalsIgnoreCase(cleaned)) {
                return valid;
            }
        }

        return "Other";
    }
}
