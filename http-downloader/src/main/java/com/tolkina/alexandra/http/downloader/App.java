package com.tolkina.alexandra.http.downloader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public static void main(String[] args) {
        Options options = new Options();
        
        Option linkOption = new Option("l", "link", true, "link to download file");
        options.addOption(linkOption);
        
        Option fileOption = new Option("f", "file", true, "file with a list of links");
        options.addOption(fileOption);
        
        Option pathOption = new Option("p", "path", true, "path to save file");
        options.addOption(pathOption);
        
        Option nameOption = new Option("n", "name", true, "name to save file");
        options.addOption(nameOption);
        
        Option threadsOption = new Option("t", "threads", true, "count of threads");
        options.addOption(threadsOption);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        String linkStr;
        String fileStr;
        String pathStr;
        String nameStr;
        String threadsStr;
        try {
            cmd = parser.parse(options, args);
            if (!cmd.hasOption("path")) {
                throw new ParseException("-p required");
            }
            if (cmd.hasOption("link")) {
                if (cmd.hasOption("file")) {
                    throw new ParseException("-f cannot be used with -l");
                }
                if (cmd.hasOption("threads")) {
                    throw new ParseException("-t cannot be used with -l");
                }
            } else {
                if (!cmd.hasOption("file")) {
                    throw new ParseException("-l or -f required");
                }
            }
            if (cmd.hasOption("file")) {
                if (cmd.hasOption("name")) {
                    throw new ParseException("-n cannot be used with -f");
                }
            }
            linkStr = cmd.getOptionValue("link");
            fileStr = cmd.getOptionValue("file");
            pathStr = cmd.getOptionValue("path");
            nameStr = cmd.getOptionValue("name");
            threadsStr = cmd.getOptionValue("threads");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("HTTPDownloader", options);
            System.exit(1);
            return;
        }
        HTTPDownloader httpDownloader = new HTTPDownloader() {
            @Override
            public void progress(double currentProgress) {
                System.out.println("Current progress: " + new BigDecimal(currentProgress * 100.0).setScale(2, RoundingMode.DOWN).doubleValue() + "%.");
            }
        };
        if (linkStr != null && pathStr != null) {
            if (nameStr != null) {
                try {
                    httpDownloader.downloadFileFromURL(linkStr, pathStr, nameStr);
                    System.out.println("File " + linkStr + " successfully downloaded to " + pathStr + File.separator + nameStr + ".");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    httpDownloader.downloadFileFromURL(linkStr, pathStr);
                    System.out.println("File " + linkStr + " successfully downloaded to " + pathStr + ".");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (fileStr != null && pathStr != null) {
            try {
                if (threadsStr != null) {
                    try {
                        int tCount = Integer.parseInt(threadsStr);
                        if (tCount < 1) {
                            throw new NumberFormatException();
                        }
                        httpDownloader.downloadFilesFromList(fileStr, pathStr, tCount);
                    } catch (NumberFormatException e) {
                        System.out.println("Parameter threads must be integer and greater than 1!");
                        return;
                    }
                } else {
                    httpDownloader.downloadFilesFromList(fileStr, pathStr);
                }
                System.out.println("Files from list " + fileStr + " successfully downloaded to " + pathStr + ".");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (ParserConfigurationException e) {
                System.out.println(e.getMessage());
            } catch (org.json.simple.parser.ParseException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
