package org.example.aiexpensecategorizer.dto;

@lombok.Getter
@lombok.Setter

public class StatementUploadResponse {
    private String fileName;
    private String  extractedData;
    public StatementUploadResponse(String fileName, String  extractedData) {
        this.fileName = fileName;
        this.extractedData = extractedData;
    }


    public String getExtractedData(){return extractedData;}
}
