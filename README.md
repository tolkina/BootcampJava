Bootcamp Java. Exposit. Task 1. HTTP Downloader
===============================================

## Synopsis

This is a Java test project. The application allows you to download files using HTTP.

## Dependencies

Java SE Runtime Environment 8. Java SE Development Kit 8. Apache Maven 3.5.0. NetBeans IDE 8.2 (Build 201609300101).

## Running

    java -jar http-downloader-VERSION.jar [-l <link> | -f <list_path>] -p <save_folder> [-n <file_name>] [-t <thread_count>]
    -l <link>           Link to the file to download. Not work with: -f. Use this if not use -f.
    -f <list_path>      The path to the file with the list of links. Not work with: -l. Use this if not use -l.
    -p <save_folder>    Folder for saving the downloaded file(s).
    -n <file_name>      Name of the file to save. Not work with: -f.
    -t <thread_count>   The maximum number of threads.
    VERSION             Current version of application.
    <link>              Link must contain the HTTP(S) at the beginning. For example https://www.example.com/file.extension
    <save_folder>       Directory must exist.
    <thread_count>      Must be greater than 1.
    
    For example:
    java -jar http-downloader-1.0.jar -l https://www.example.com/file.zip -p c:/downloads -n archive.zip -t 5
    
## Author

**Full name**: Tolkina Alexandra Andreevna \
**Email**: tolkina_aa_14@mf.grsu.by

Bootcamp Java. Exposit. Task 2. CSV Finder
==========================================

## Synopsis

This is a Java test project. The application allows you to find any string into csv-file and write found columns to some output file.

## Dependencies

Java SE Runtime Environment 8. Java SE Development Kit 8. Apache Maven 3.5.0. NetBeans IDE 8.2 (Build 201609300101).

## Running

    java -jar csv-finder-VERSION.jar -i <input_file> -q <search_string> -o <output_file>
    -i <input_file>     Input file. Required.
    -q <search_string>  Search string. Required.
    -o <output_file>    Output file. Required.
    VERSION             Current version of application.
    <input_file>        Must have CSV format.
    <output_file>       Must have CSV or TXT format.
    
## Author

**Full name**: Tolkina Alexandra Andreevna \
**Email**: tolkina_aa_14@mf.grsu.by
