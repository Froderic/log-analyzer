package com.woo.loganalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class CSVExporter {

    private String filename;

    public CSVExporter(String filename) {
        this.filename = filename;
    }

    /**
     * Export log level statistics to CSV
     * Format: Level,Count,Percentage
     */
    public void exportLevelStats(Map<String, Integer> levelCounts, int totalLogs) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Level,Count,Percentage");

            // Write data rows
            for (Map.Entry<String, Integer> entry : levelCounts.entrySet()) {
                String level = entry.getKey();
                int count = entry.getValue();
                double percentage = (count * 100.0) / totalLogs;

                writer.printf("%s,%d,%.2f%%\n", level, count, percentage);
            }

            System.out.println("Statistics exported to: " + filename);
        }
    }

    /**
     * Export time-based statistics to CSV
     * Format: Period,Count
     */
    public void exportTimeStats(Map<String, Integer> timeCounts, String timeType) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println(timeType + ",Count");

            // Write data rows (sorted by period)
            timeCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> writer.printf("%s,%d\n", entry.getKey(), entry.getValue()));

            System.out.println("Time statistics exported to: " + filename);
        }
    }

    /**
     * Export top N messages to CSV
     * Format: Message,Count,Percentage
     */
    public void exportTopMessages(List<Map.Entry<String, Integer>> topMessages, int totalLogs) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Message,Count,Percentage");

            // Write data rows
            for (Map.Entry<String, Integer> entry : topMessages) {
                String message = entry.getKey().replace(",", ";"); // Escape commas in messages
                int count = entry.getValue();
                double percentage = (count * 100.0) / totalLogs;

                writer.printf("\"%s\",%d,%.2f%%\n", message, count, percentage);
            }

            System.out.println("Top messages exported to: " + filename);
        }
    }
}