package com.tolkina.alexandra.http.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPDownloader {
    private static final int BUFFER_SIZE = 4096;
    
    public static void downloadFileFromURL(String fileURL, String saveDir, String saveName) throws IOException {
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

    public static void downloadFileFromURL(String fileURL, String saveDir) throws IOException {
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
}
