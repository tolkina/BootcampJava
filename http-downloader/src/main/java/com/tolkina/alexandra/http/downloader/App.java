package com.tolkina.alexandra.http.downloader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
                if (!cmd.hasOption("name")) {
                    throw new ParseException("-n required if used -l");
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
    }
}
