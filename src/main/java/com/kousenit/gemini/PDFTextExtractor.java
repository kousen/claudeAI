package com.kousenit.gemini;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

public class PDFTextExtractor {
    public int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Split the string based on spaces and punctuation.
        String[] words = text.split("\\s+|,|\\.|\\(|\\)|\\[|]|!|\\?|;|:");
        return words.length;
    }

    public String extractText(String pdfFilePath) throws IOException, TikaException, SAXException {
        // Create a content handler
        BodyContentHandler handler = new BodyContentHandler(-1);

        // Create a metadata object
        Metadata metadata = new Metadata();
        try (FileInputStream inputstream = new FileInputStream(pdfFilePath)) {
            // Skip OCR processing
            ParseContext context = new ParseContext();
            TesseractOCRConfig config = new TesseractOCRConfig();
            config.setSkipOcr(true);
            context.set(TesseractOCRConfig.class, config);

            // Parse the PDF file
            PDFParser pdfParser = new PDFParser();
            pdfParser.parse(inputstream, handler, metadata, context);
        }

        return handler.toString();
    }


    public static void main(String[] args) throws IOException, TikaException, SAXException {
        // Path to the PDF file
        String pdfFilePath = "src/main/resources/help-your-boss-help-you_P1.0.pdf";
        var extractor = new PDFTextExtractor();
        String text = extractor.extractText(pdfFilePath);
        System.out.println("Approximate word count:" + extractor.countWords(text));
    }
}
