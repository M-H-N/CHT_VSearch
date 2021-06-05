package cht_v_search.utils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Utility {
    public static List<Integer> getAllOccurrencesOf(String source, String query) {
        List<Integer> result = new ArrayList<>();
        int index = source.indexOf(query);
        while (index >= 0) {
            result.add(index);
            index = source.indexOf(query, index + query.length());
        }
        return result;
    }

    public static String unifyUnicodeString(String source) {
//        return source;
        return source.replaceAll("ك", "ک").replaceAll("ي", "ی");
    }

    public static String extractAllTextsInPdf(File file) {
        if (file == null || !file.getName().toLowerCase().endsWith(".pdf")) return null;
        PdfReader pdfReader = null;
        try {
            StringBuilder resultStringBuilder = new StringBuilder();
            pdfReader = new PdfReader(file.getAbsolutePath());
            final var contentParser = new PdfReaderContentParser(pdfReader);
            TextExtractionStrategy strategy;
            final int pagesCount = pdfReader.getNumberOfPages();
            for (int i = 0; i < pagesCount; i++) {
                try {
                    strategy = contentParser.processContent(i, new SimpleTextExtractionStrategy());
                    resultStringBuilder.append(strategy.getResultantText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return resultStringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (pdfReader != null) pdfReader.close();
        }
    }
}
