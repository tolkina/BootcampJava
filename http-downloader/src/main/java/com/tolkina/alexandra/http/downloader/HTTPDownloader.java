package com.tolkina.alexandra.http.downloader;

import com.sun.media.sound.InvalidFormatException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HTTPDownloader {
    private static final int BUFFER_SIZE = 4096;
    
    public void downloadFileFromURL(String fileURL, String saveDir, String saveName) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        int respCode = httpConn.getResponseCode();
        if (respCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + saveName;
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } else {
            throw new MalformedURLException("Error file download. Response code: " + respCode + ".");
        }
        httpConn.disconnect();
    }

    public void downloadFileFromURL(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        int respCode = httpConn.getResponseCode();
        if (respCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            if (disposition != null) {
                int fileNameIndex = disposition.indexOf("filename=");
                if (fileNameIndex > 0) {
                    fileName = disposition.substring(fileNameIndex + 10, disposition.length() - 1);
                }
            } else {
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
            }
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } else {
            throw new MalformedURLException("Error file download. Response code: " + respCode + ".");
        }
        httpConn.disconnect();
    }

    public void downloadFilesFromList(String listFilePath, String saveDir) throws IOException {
        String fileListExtension = FilenameUtils.getExtension(listFilePath);
        if (fileListExtension.equalsIgnoreCase("csv")) {
            File listFile = new File(listFilePath);
            CSVParser csvParser = CSVParser.parse(listFile, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            List<CSVRecord> records = csvParser.getRecords();
            List<String[]> URLFileNamePairs = new ArrayList<>();
            for (CSVRecord record : records) {
                if (record.size() == 1) {
                    URLFileNamePairs.add(new String[] {record.get(0)});
                } else if (record.size() == 2) {
                    URLFileNamePairs.add(new String[]{record.get(0), record.get(1)});
                } else {
                    throw new IOException("Invalid column count. Line number: " + record.getRecordNumber() + ".");
                }
            }
            File logFile = new File(saveDir + File.separator + "log.txt");
            PrintWriter logWriter = new PrintWriter(logFile);
            double currentProgress = 0.0;
            double stepProgress = 1.0 / URLFileNamePairs.size();
            for (String[] URLFileNamePair : URLFileNamePairs) {
                if (URLFileNamePair.length == 1) {
                    try {
                        downloadFileFromURL(URLFileNamePair[0], saveDir);
                        logWriter.println("[Successfully] " + URLFileNamePair[0]);
                    } catch (IOException e) {
                        logWriter.println("[   Error    ] " + URLFileNamePair[0] + " <" + e.getMessage() + ">.");
                    }
                } else if (URLFileNamePair.length == 2) {
                    try {
                        downloadFileFromURL(URLFileNamePair[0], saveDir, URLFileNamePair[1]);
                        logWriter.println("[Successfully] " + URLFileNamePair[0]);
                    } catch (IOException e) {
                        logWriter.println("[   Error    ] " + URLFileNamePair[0] + " <" + e.getMessage() + ">.");
                    }
                }
                currentProgress += stepProgress;
                progress(currentProgress);
            }
            logWriter.close();
        } else if (fileListExtension.equalsIgnoreCase("xml")) {

        } else if (fileListExtension.equalsIgnoreCase("json")) {

        } else {
            throw new InvalidFormatException("File with list of links must be CSV, XML or JSON.");
        }
    }

    public void progress(double currentProgress) {
    }
}
