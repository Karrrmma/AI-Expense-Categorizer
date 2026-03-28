package org.example.aiexpensecategorizer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiCategoryResult {
    private int index;
    private int category;

    public AiCategoryResult(int index, int category) {
        this.index= index;
        this.category = category;
    }



}
