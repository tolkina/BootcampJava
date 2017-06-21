package com.tolkina.alexandra.csv.finder;

import java.io.IOException;
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
        Option inputOption = new Option("i", "input", true, "input file");
        inputOption.setRequired(true);
        options.addOption(inputOption);
        Option questOption = new Option("q", "quest", true, "quest string");
        questOption.setRequired(true);
        options.addOption(questOption);
        Option outputOption = new Option("o", "output", true, "output file");
        outputOption.setRequired(true);
        options.addOption(outputOption);
        CommandLineParser cmdParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd;
        String inputString, questString, outputString;
        try {
            cmd = cmdParser.parse(options, args);
            inputString = cmd.getOptionValue("input");
            questString = cmd.getOptionValue("quest");
            outputString = cmd.getOptionValue("output");
        } catch (ParseException ex) {
            helpFormatter.printHelp("csv-finder", options);
            System.out.println(ex.getMessage());
            System.exit(1);
            return;
        }
        try {
            CSVFinder.find(inputString, questString, outputString);
            System.out.println("Finished!");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
