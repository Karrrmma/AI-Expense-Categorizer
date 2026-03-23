package org.example.aiexpensecategorizer.Service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class PdfExtractionService {
    public String extractPdf(MultipartFile pdfFile) {
        try(PDDocument pdfDocument =  Loader.loadPDF( new RandomAccessReadBuffer(pdfFile.getInputStream()))) {
            PDFTextStripper pdfTextStripper  = new PDFTextStripper();
            return pdfTextStripper.getText(pdfDocument);



        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
