package org.example.aiexpensecategorizer.dto;

import java.util.List;

@lombok.Getter
@lombok.Setter

public class StatementUploadResponse {
    private String fileName;
    private String  extractedData;
    private List<TransactionDTO> transactionDTOList;

    public StatementUploadResponse(String fileName, String  extractedData, List<TransactionDTO> transactionDTOList) {
        this.fileName = fileName;
        this.extractedData = extractedData;
        this.transactionDTOList = transactionDTOList;

    }



}
