package com.woo.loganalyzer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.Callable;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Command(
        name = "loganalyzer",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Analyzes log files and provides statistics and filtering capabilities"
)
public class LogAnalyzerApp implements Callable<Integer> {

    @Parameters(
            index = "0",
            description = "Path to the log file to analyze"
    )
    private File logFile;

    @Option(
            names = {"-c", "--count"},
            description = "Display total line count"
    )
    private boolean showCount;

    @Option(
            names = {"-l", "--level"},
            description = "Filter by log level (INFO, ERROR, WARN, DEBUG)"
    )
    private String logLevel;

    @Option(
            names = {"-s", "--search"},
            description = "Search for lines containing specified text"
    )
    private String searchTerm;

    @Option(
            names = {"--from"},
            description = "Filter logs from this date (format: YYYY-MM-DD)"
    )
    private String fromDate;

    @Option(
            names = {"--to"},
            description = "Filter logs until this date (format: YYYY-MM-DD)"
    )
    private String toDate;

    @Option(
            names = {"-r", "--regex"},
            description = "Search using regex pattern"
    )
    private String regexPattern;

    @Override
    public Integer call() throws Exception {
        // Validate file exists
        if (!logFile.exists()) {
            System.err.println("Error: File not found - " + logFile.getPath());
            return 1;
        }

        if (!logFile.isFile()) {
            System.err.println("Error: Path is not a file - " + logFile.getPath());
            return 1;
        }

        System.out.println("Analyzing: " + logFile.getName());
        System.out.println("File size: " + formatFileSize(logFile.length()));

        if (showCount) {
            long lineCount = countLines();
            System.out.println("Total lines: " + lineCount);
        }

        // Check if any filtering/searching is requested
        boolean hasFilters = (logLevel != null || fromDate != null || toDate != null || searchTerm != null || regexPattern != null);

        if (hasFilters) {
            // Display what filters are active
            if (logLevel != null) {
                System.out.println("Filter: level = " + logLevel.toUpperCase());
            }
            if (fromDate != null) {
                System.out.println("Filter: from = " + fromDate);
            }
            if (toDate != null) {
                System.out.println("Filter: to = " + toDate);
            }
            if (searchTerm != null) {
                System.out.println("Filter: search = '" + searchTerm + "'");
            }
            if (regexPattern != null) {
                System.out.println("Filter: regex = '" + regexPattern + "'");
            }
            System.out.println("---");

            displayAllFilters();
        }

        return 0;
    }

    private long countLines() throws Exception {
        long count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            while (reader.readLine() != null) {
                count++;
            }
        }

        return count;
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }



    private boolean matchesDateFilter(String line) {
        // If no date filters specified, include all lines
        if (fromDate == null && toDate == null) {
            return true;
        }

        // Extract date from log line (first 10 characters: "2024-12-09")
        if (line.length() < 10) {
            return false; // Line too short to contain a date
        }

        String logDate = line.substring(0, 10);

        try {
            LocalDate lineDate = LocalDate.parse(logDate);

            // Check from date
            if (fromDate != null) {
                LocalDate from = LocalDate.parse(fromDate);
                if (lineDate.isBefore(from)) {
                    return false;
                }
            }

            // Check to date
            if (toDate != null) {
                LocalDate to = LocalDate.parse(toDate);
                if (lineDate.isAfter(to)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // If date parsing fails, exclude the line
            return false;
        }
    }

    private void displayAllFilters() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            int matchCount = 0;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Apply date filter
                if (!matchesDateFilter(line)) {
                    continue;
                }

                // Apply level filter (if specified)
                if (logLevel != null && !line.contains(logLevel.toUpperCase())) {
                    continue;
                }

                // Apply search filter (if specified)
                if (searchTerm != null && !line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }

                // Apply regex filter (if specified)
                if (regexPattern != null) {
                    Pattern pattern = Pattern.compile(regexPattern);
                    Matcher matcher = pattern.matcher(line);
                    if (!matcher.find()) {
                        continue;
                    }
                }

                // If we get here, line matches ALL filters
                if (searchTerm != null || regexPattern != null) {
                    // Show line numbers when searching
                    System.out.println("[Line " + lineNumber + "] " + line);
                } else {
                    System.out.println(line);
                }
                matchCount++;
            }

            System.out.println("\n--- Found " + matchCount + " matching lines ---");
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new LogAnalyzerApp()).execute(args);
        System.exit(exitCode);
    }

}