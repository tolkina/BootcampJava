package com.tolkina.alexandra.csv.finder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;

public class CSVFinder {
    public static void find(String inputFilePath, String questString, String outputFilePath) throws IOException {
        String inputFileExtension = FilenameUtils.getExtension(inputFilePath);
        if (!inputFileExtension.equalsIgnoreCase("csv")) {
            throw new IOException("Expected CSV-file.");
        }
        File inputFile = new File(inputFilePath);
        FileReader inputFileReader = new FileReader(inputFile);
        CSVParser csvParser = new CSVParser(inputFileReader, CSVFormat.DEFAULT);
        List<CSVRecord> records = csvParser.getRecords();
        int rowCount = records.size();
        if (rowCount > 0) {
            int columnCount = records.get(0).size();
            List<String[]> dataList = new ArrayList<>();
            for (int j = 0; j < columnCount; ++j) {
                boolean needToAdd = false;
                String[] bufferColumn = new String[rowCount];
                for (int i = 0; i < rowCount; ++i) {
                    bufferColumn[i] = records.get(i).get(j);
                    if (!needToAdd) {
                        Pattern pattern = Pattern.compile(questString);
                        Matcher matcher = pattern.matcher(bufferColumn[i]);
                        if (matcher.matches()) {
                            needToAdd = true;
                        }
                    }
                }
                if (needToAdd) {
                    dataList.add(bufferColumn);
                }
            }
            String[][] data = new String[0][0];
            Printer printer;
            String outputFileExtension = FilenameUtils.getExtension(outputFilePath).toLowerCase();
            switch (outputFileExtension) {
                case "csv": {
                    printer = new CSVPrinter();
                } break;
                case "txt": {
                    printer = new TXTPrinter();
                } break;
                default: {
                    throw new IOException("Incorrect output file format.");
                }
            }
            if (dataList.size() > 0) {
                data = new String[rowCount][dataList.size()];
                for (int i = 0; i < rowCount; ++i) {
                    for (int j = 0; j < dataList.size(); ++j) {
                        data[i][j] = dataList.get(j)[i];
                    }
                }
            }
            printer.print(data, outputFilePath);
        }
    }
}
