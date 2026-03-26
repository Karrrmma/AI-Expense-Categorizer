package org.example.aiexpensecategorizer.Parser;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

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

    public String categorizeTransaction(String description, double amount) {
        try {
            String prompt = """
                    You are a financial transaction classifier.

                    Choose exactly one category from this list:
                    Food & Dining, Groceries, Shopping, Transportation, Bills & Utilities,
                    Entertainment, Health & Personal Care, Travel, Transfers & Payments,
                    Fees & Interest, Other.

                    Transaction description: %s
                    Transaction amount: %.2f

                    Return only the category name.
                    """.formatted(description, amount);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("OpenAI raw response: " + response);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            return sanitizeCategory(content);

        } catch (Exception e) {
            e.printStackTrace();
            return "Other";
        }
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
