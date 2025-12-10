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

import java.util.Map;
import java.util.HashMap;

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

    @Option(
            names = {"--stats"},
            description = "Show log level statistics (count and percentage)"
    )
    private boolean showStats;

    @Option(
            names = {"--time-stats"},
            description = "Show time-based statistics (hourly or daily)"
    )
    private String timeStats;

    @Option(
            names = {"--top"},
            description = "Show top N most frequent log messages"
    )
    private Integer topN;

    @Option(
            names = {"--summary"},
            description = "Show comprehensive summary report"
    )
    private boolean showSummary;

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
        boolean hasFilters = (logLevel != null || fromDate != null || toDate != null || searchTerm != null
                                || regexPattern != null || showStats || timeStats != null || topN != null
                                || showSummary);

        if (hasFilters || showStats) {
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

            if (showStats) {
                displayStatistics();
            } else if (timeStats != null) {
                displayTimeStatistics();
            } else if (topN != null) {
                displayTopMessages();
            } else if (showSummary) {
                displaySummary();
            } else {
                displayAllFilters();
            }
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
                    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
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

    private void displayStatistics() throws Exception {
        // Count logs by level
        Map<String, Integer> levelCounts = new HashMap<>();
        int totalLogsTemp = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Apply date filter if specified
                if (!matchesDateFilter(line)) {
                    continue;
                }

                // Apply search filter if specified
                if (searchTerm != null && !line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }

                // Apply regex filter if specified
                if (regexPattern != null) {
                    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(line);
                    if (!matcher.find()) {
                        continue;
                    }
                }

                // Apply level filter if specified
                if (logLevel != null && !line.contains(logLevel.toUpperCase())) {
                    continue;
                }

                // Extract log level from line
                String level = extractLogLevel(line);
                if (level != null) {
                    levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
                    totalLogsTemp++;
                }
            }
        }
        final int totalLogs = totalLogsTemp;

        // Display statistics
        if (totalLogs == 0) {
            System.out.println("No logs to analyze.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("LOG LEVEL STATISTICS");
        System.out.println("=".repeat(60));
        System.out.println(String.format("%-15s %-15s %-15s", "Level", "Count", "Percentage"));
        System.out.println("-".repeat(60));

        // Sort by count (descending)
        levelCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    String level = entry.getKey();
                    int count = entry.getValue();
                    double percentage = (count * 100.0) / totalLogs;
                    System.out.println(String.format("%-15s %-15d %-15.2f%%",
                            level, count, percentage));
                });

        System.out.println("-".repeat(60));
        System.out.println(String.format("%-15s %-15d", "TOTAL", totalLogs));
        System.out.println("=".repeat(60) + "\n");
    }

    private String extractLogLevel(String line) {
        // Common log levels to look for
        String[] levels = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE", "FATAL"};

        for (String level : levels) {
            if (line.contains(level)) {
                return level;
            }
        }

        return null; // No level found
    }

    private void displayTimeStatistics() throws Exception {
        if (timeStats == null || (!timeStats.equals("hourly") && !timeStats.equals("daily"))) {
            System.err.println("Error: --time-stats must be 'hourly' or 'daily'");
            return;
        }

        Map<String, Integer> timeCounts = new HashMap<>();
        int totalLogs = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Apply all filters
                if (!matchesDateFilter(line)) {
                    continue;
                }

                if (logLevel != null && !line.contains(logLevel.toUpperCase())) {
                    continue;
                }

                if (searchTerm != null && !line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }

                if (regexPattern != null) {
                    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(line);
                    if (!matcher.find()) {
                        continue;
                    }
                }

                // Extract time period from line
                String timePeriod = extractTimePeriod(line, timeStats);
                if (timePeriod != null) {
                    timeCounts.put(timePeriod, timeCounts.getOrDefault(timePeriod, 0) + 1);
                    totalLogs++;
                }
            }
        }

        if (totalLogs == 0) {
            System.out.println("No logs to analyze.");
            return;
        }

        final int finalTotal = totalLogs;

        // Display statistics
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TIME-BASED STATISTICS (" + timeStats.toUpperCase() + ")");
        System.out.println("=".repeat(60));
        System.out.println(String.format("%-25s %-15s %-15s", "Time Period", "Count", "Percentage"));
        System.out.println("-".repeat(60));

        // Sort by time period (chronological)
        timeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String period = entry.getKey();
                    int count = entry.getValue();
                    double percentage = (count * 100.0) / finalTotal;
                    System.out.println(String.format("%-25s %-15d %-15.2f%%",
                            period, count, percentage));
                });

        System.out.println("-".repeat(60));
        System.out.println(String.format("%-25s %-15d", "TOTAL", finalTotal));
        System.out.println("=".repeat(60) + "\n");
    }

    private String extractTimePeriod(String line, String mode) {
        // Log format: "2024-12-09 10:00:01 INFO ..."
        if (line.length() < 19) {
            return null;
        }

        try {
            if (mode.equals("daily")) {
                // Extract date: "2024-12-09"
                return line.substring(0, 10);
            } else if (mode.equals("hourly")) {
                // Round to hour: "2024-12-09 10:00"
                return line.substring(0, 13) + ":00";
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private void displayTopMessages() throws Exception {
        if (topN == null || topN <= 0) {
            System.err.println("Error: --top must be a positive number");
            return;
        }

        Map<String, Integer> messageCounts = new HashMap<>();
        int totalLogs = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Apply all filters
                if (!matchesDateFilter(line)) {
                    continue;
                }

                if (logLevel != null && !line.contains(logLevel.toUpperCase())) {
                    continue;
                }

                if (searchTerm != null && !line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }

                if (regexPattern != null) {
                    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(line);
                    if (!matcher.find()) {
                        continue;
                    }
                }

                // Extract message content (everything after log level)
                String message = extractMessage(line);
                if (message != null) {
                    messageCounts.put(message, messageCounts.getOrDefault(message, 0) + 1);
                    totalLogs++;
                }
            }
        }

        if (totalLogs == 0) {
            System.out.println("No logs to analyze.");
            return;
        }

        final int finalTotal = totalLogs;

        // Display statistics
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TOP " + topN + " MOST FREQUENT LOG MESSAGES");
        System.out.println("=".repeat(80));
        System.out.println(String.format("%-50s %-12s %-12s", "Message", "Count", "Percentage"));
        System.out.println("-".repeat(80));

        // Sort by count descending and limit to topN
        messageCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(topN)
                .forEach(entry -> {
                    String message = entry.getKey();
                    int count = entry.getValue();
                    double percentage = (count * 100.0) / finalTotal;

                    // Truncate long messages
                    String displayMessage = message.length() > 47 ?
                            message.substring(0, 47) + "..." : message;

                    System.out.println(String.format("%-50s %-12d %-12.2f%%",
                            displayMessage, count, percentage));
                });

        System.out.println("-".repeat(80));
        System.out.println(String.format("%-50s %-12d", "TOTAL LOGS ANALYZED", finalTotal));
        System.out.println("=".repeat(80) + "\n");
    }

    private String extractMessage(String line) {
        // Log format: "2024-12-09 10:00:01 INFO Message content here"
        // We want to extract "Message content here"

        String[] levels = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE", "FATAL"};

        for (String level : levels) {
            int levelIndex = line.indexOf(level);
            if (levelIndex != -1) {
                // Get everything after the level and trim whitespace
                int messageStart = levelIndex + level.length();
                if (messageStart < line.length()) {
                    return line.substring(messageStart).trim();
                }
            }
        }

        return null;
    }

    private void displaySummary() throws Exception {
        Map<String, Integer> levelCounts = new HashMap<>();
        Map<String, Integer> dateCounts = new HashMap<>();
        Map<String, Integer> messageCounts = new HashMap<>();
        int totalLogs = 0;
        String firstDate = null;
        String lastDate = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Apply filters
                if (!matchesDateFilter(line)) {
                    continue;
                }

                if (logLevel != null && !line.contains(logLevel.toUpperCase())) {
                    continue;
                }

                if (searchTerm != null && !line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }

                if (regexPattern != null) {
                    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(line);
                    if (!matcher.find()) {
                        continue;
                    }
                }

                totalLogs++;

                // Track log levels
                String level = extractLogLevel(line);
                if (level != null) {
                    levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
                }

                // Track dates
                if (line.length() >= 10) {
                    String date = line.substring(0, 10);
                    dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);

                    if (firstDate == null || date.compareTo(firstDate) < 0) {
                        firstDate = date;
                    }
                    if (lastDate == null || date.compareTo(lastDate) > 0) {
                        lastDate = date;
                    }
                }

                // Track messages
                String message = extractMessage(line);
                if (message != null) {
                    messageCounts.put(message, messageCounts.getOrDefault(message, 0) + 1);
                }
            }
        }

        if (totalLogs == 0) {
            System.out.println("No logs to analyze.");
            return;
        }

        final int finalTotal = totalLogs;

        // Display comprehensive summary
        System.out.println("\n" + "=".repeat(70));
        System.out.println("COMPREHENSIVE LOG SUMMARY");
        System.out.println("=".repeat(70));

        // Basic info
        System.out.println("\n[ OVERVIEW ]");
        System.out.println("  Total logs analyzed: " + totalLogs);
        System.out.println("  Date range: " + firstDate + " to " + lastDate);
        System.out.println("  Unique dates: " + dateCounts.size());
        System.out.println("  Unique messages: " + messageCounts.size());

        // Log level breakdown
        System.out.println("\n[ LOG LEVEL DISTRIBUTION ]");
        levelCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / finalTotal;
                    System.out.println(String.format("  %-10s %5d  (%5.1f%%)",
                            entry.getKey(), entry.getValue(), percentage));
                });

        // Busiest dates
        System.out.println("\n[ TOP 5 BUSIEST DATES ]");
        dateCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / finalTotal;
                    System.out.println(String.format("  %s  %5d logs  (%5.1f%%)",
                            entry.getKey(), entry.getValue(), percentage));
                });

        // Most common messages
        System.out.println("\n[ TOP 5 MOST FREQUENT MESSAGES ]");
        messageCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    String msg = entry.getKey().length() > 45 ?
                            entry.getKey().substring(0, 45) + "..." : entry.getKey();
                    System.out.println(String.format("  [%2d] %s", entry.getValue(), msg));
                });

        // Health indicators
        System.out.println("\n[ HEALTH INDICATORS ]");
        int errorCount = levelCounts.getOrDefault("ERROR", 0);
        int warnCount = levelCounts.getOrDefault("WARN", 0);
        double errorRate = (errorCount * 100.0) / finalTotal;
        double warnRate = (warnCount * 100.0) / finalTotal;

        System.out.println(String.format("  Error rate: %.1f%% (%d errors)", errorRate, errorCount));
        System.out.println(String.format("  Warning rate: %.1f%% (%d warnings)", warnRate, warnCount));

        if (errorRate > 20) {
            System.out.println("  [!] HIGH ERROR RATE - Investigation recommended");
        } else if (errorRate > 10) {
            System.out.println("  [!] ELEVATED ERROR RATE - Monitor closely");
        } else {
            System.out.println("  [OK] Error rate within normal range");
        }

        System.out.println("=".repeat(70) + "\n");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new LogAnalyzerApp()).execute(args);
        System.exit(exitCode);
    }

}