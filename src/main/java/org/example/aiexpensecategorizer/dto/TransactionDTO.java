package org.example.aiexpensecategorizer.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class TransactionDTO {
    private String date;
    private String description;
    private double amount;
    private String category;

    public TransactionDTO(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }
    public TransactionDTO(String date, String description, double amount, String category) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }




}
