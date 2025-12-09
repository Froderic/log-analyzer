package com.woo.loganalyzer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.Callable;

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

            // If level filter is specified, display filtered logs
            if (logLevel != null) {
                System.out.println("Filtering by level: " + logLevel.toUpperCase());
                System.out.println("---");
                displayFilteredLogs();
            }

            // If search term is specified, search logs
            if (searchTerm != null) {
                System.out.println("Searching for: '" + searchTerm + "'");
                System.out.println("---");
                displaySearchResults();
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

    private void displayFilteredLogs() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            int matchCount = 0;

            while ((line = reader.readLine()) != null) {
                if (logLevel == null || line.contains(logLevel.toUpperCase())) {
                    System.out.println(line);
                    matchCount++;
                }
            }

            System.out.println("\n--- Found " + matchCount + " matching lines ---");
        }
    }

    private void displaySearchResults() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            int matchCount = 0;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    System.out.println("[Line " + lineNumber + "] " + line);
                    matchCount++;
                }
            }

            System.out.println("\n--- Found " + matchCount + " matches for '" + searchTerm + "' ---");
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new LogAnalyzerApp()).execute(args);
        System.exit(exitCode);
    }

}