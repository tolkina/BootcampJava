package com.tolkina.alexandra.http.downloader;

import com.sun.media.sound.InvalidFormatException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public void downloadFilesFromList(String listFilePath, String saveDir) throws IOException, ParserConfigurationException, ParseException {
        String fileListExtension = FilenameUtils.getExtension(listFilePath);
        File logFile = new File(saveDir + File.separator + "log.txt");
        PrintWriter logWriter = new PrintWriter(logFile);
        double currentProgress, stepProgress;
        if (fileListExtension.equalsIgnoreCase("csv")) {
            File listFile = new File(listFilePath);
            CSVParser csvParser = CSVParser.parse(listFile, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            List<CSVRecord> records = csvParser.getRecords();
            currentProgress = 0.0;
            stepProgress = 1.0 / records.size();
            for (CSVRecord record : records) {
                if (record.size() == 1) {
                    try {
                        downloadFileFromURL(record.get(0), saveDir);
                        logWriter.println("[Successfully] " + record.get(0));
                    } catch (IOException e) {
                        logWriter.println("[   Error    ] " + record.get(0) + " <" + e.getMessage() + ">");
                    }
                } else if (record.size() == 2) {
                    try {
                        downloadFileFromURL(record.get(0), saveDir, record.get(1));
                        logWriter.println("[Successfully] " + record.get(0));
                    } catch (IOException e) {
                        logWriter.println("[   Error    ] " + record.get(0) + " <" + e.getMessage() + ">");
                    }
                } else {
                    throw new IOException("Invalid column count. Line number: " + record.getRecordNumber() + ".");
                }
                currentProgress += stepProgress;
                progress(currentProgress);
            }
        } else if (fileListExtension.equalsIgnoreCase("xml")) {
            File fXmlFile = new File(listFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            try {
                doc = dBuilder.parse(fXmlFile);
            } catch (SAXException e) {
                throw new IOException("Bad XML file. Error: " + e.getMessage() + ".");
            }
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("file");
            currentProgress = 0.0;
            stepProgress = 1.0 / nList.getLength();
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (eElement.getElementsByTagName("link").getLength() == 0) {
                        throw new IOException("Bad XML file. No link.");
                    } else {
                        if (eElement.getElementsByTagName("name").getLength() == 0) {
                            String link = eElement.getElementsByTagName("link").item(0).getTextContent();
                            try {
                                downloadFileFromURL(link, saveDir);
                                logWriter.println("[Successfully] " + link);
                            } catch (IOException e) {
                                logWriter.println("[   Error    ] " + link + " <" + e.getMessage() + ">");
                            }
                        } else {
                            String link = eElement.getElementsByTagName("link").item(0).getTextContent();
                            try {
                                downloadFileFromURL(link, saveDir, eElement.getElementsByTagName("name").item(0).getTextContent());
                                logWriter.println("[Successfully] " + link);
                            } catch (IOException e) {
                                logWriter.println("[   Error    ] " + link + " <" + e.getMessage() + ">");
                            }
                        }
                    }
                }
                currentProgress += stepProgress;
                progress(currentProgress);
            }
        } else if (fileListExtension.equalsIgnoreCase("json")) {
            File jsonFile = new File(listFilePath);
            JSONParser jsonParser = new JSONParser();
            ArrayList<JSONObject> list = (ArrayList<JSONObject>)jsonParser.parse(FileUtils.readFileToString(jsonFile));
            currentProgress = 0.0;
            stepProgress = 1.0 / list.size();
            for (JSONObject obj : list) {
                if (obj.containsKey("link") && obj.containsKey("name")) {
                    String link = (String)obj.get("link");
                    String name = (String)obj.get("name");
                    try {
                        downloadFileFromURL(link, saveDir, name);
                        logWriter.println("[Successfully] " + link);
                    } catch (IOException e) {
                        logWriter.println("[   Error    ] " + link + " <" + e.getMessage() + ">");
                    }
                } else if (obj.containsKey("link")) {
                    String link = (String)obj.get("link");
                    try {
                        downloadFileFromURL(link, saveDir);
                        logWriter.println("[Successfully] " + link);
                    } catch (IOException e) {
                        logWriter.println("[   Error    ] " + link + " <" + e.getMessage() + ">");
                    }
                }
                currentProgress += stepProgress;
                progress(currentProgress);
            }
        } else {
            throw new InvalidFormatException("File with list of links must be CSV, XML or JSON.");
        }
        logWriter.close();
    }

    public void progress(double currentProgress) {
    }
}
