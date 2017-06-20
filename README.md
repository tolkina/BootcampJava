Bootcamp Java. Exposit. Task 1. Http downloader
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
    
## Author

**Full name**: Tolkina Alexandra Andreevna \
**Email**: tolkina_aa_14@mf.grsu.by
