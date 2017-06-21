package com.tolkina.alexandra.csv.finder;

import java.io.IOException;

public interface Printer {
    public void print(String[][] data, String outputFilePath) throws IOException;
}
