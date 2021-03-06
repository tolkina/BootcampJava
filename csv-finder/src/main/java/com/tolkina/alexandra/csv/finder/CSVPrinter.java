package com.tolkina.alexandra.csv.finder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;

public class CSVPrinter implements Printer {

    private final String delimeter = ",";
    private final String lineBreak = "\r\n";
    
    @Override
    public void print(String[][] data, String outputFilePath) throws IOException {
        String outputFileExtension = FilenameUtils.getExtension(outputFilePath);
        if (!outputFileExtension.equalsIgnoreCase("csv")) {
            throw new IOException("Expected CSV-file.");
        }
        File outputFile = new File(outputFilePath);
        try (FileWriter outputFileWriter = new FileWriter(outputFile)) {
            int rows = data.length;
            if (rows > 0) {
                int cols = data[0].length;
                for (int j = 0; j < cols - 1; ++j) {
                    for (int i = 0; i < rows - 1; ++i) {
                        outputFileWriter.write(data[i][j] + delimeter);
                    }
                    outputFileWriter.write(data[rows - 1][j] + lineBreak);
                }
                for (int i = 0; i < rows - 1; ++i) {
                    outputFileWriter.write(data[i][cols - 1] + delimeter);
                }
                outputFileWriter.write(data[rows - 1][cols - 1]);
            }
        }
    }
    
}
