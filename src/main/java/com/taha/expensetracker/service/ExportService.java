package com.taha.expensetracker.service;

import com.taha.expensetracker.model.Transaction;
import com.taha.expensetracker.model.record.CategorySummary;
import com.taha.expensetracker.model.record.DashboardSummary;
import com.taha.expensetracker.model.record.MonthlySummary;
import com.taha.expensetracker.model.sealed.ExportRequest;
import com.taha.expensetracker.model.sealed.TransactionExportRequest;
import com.taha.expensetracker.model.sealed.SummaryExportRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class ExportService {

    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public Path exportTransactionsToCsv(List<Transaction> transactions, String exportFolder) {
        Objects.requireNonNull(transactions, "Transactions list must not be null.");
        Path exportDir = createExportDirectory(exportFolder);

        String fileName = "transactions_" + LocalDateTime.now().format(FILE_TIMESTAMP) + ".csv";
        Path filePath = exportDir.resolve(fileName);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Amount,Type,Category,Date,Notes").append(System.lineSeparator());

        for (Transaction transaction : transactions) {
            csv.append(safe(transaction.getId()))
                    .append(",")
                    .append(escapeCsv(transaction.getTitle()))
                    .append(",")
                    .append(safe(transaction.getAmount()))
                    .append(",")
                    .append(escapeCsv(transaction.getType().getDisplayName()))
                    .append(",")
                    .append(escapeCsv(transaction.getCategory().getName()))
                    .append(",")
                    .append(safe(transaction.getTransactionDate()))
                    .append(",")
                    .append(escapeCsv(transaction.getNotes()))
                    .append(System.lineSeparator());
        }

        writeFile(filePath, csv.toString());
        return filePath;
    }

    public Path exportSummaryToTxt(DashboardSummary dashboardSummary,
                                   List<CategorySummary> categorySummaries,
                                   List<MonthlySummary> monthlySummaries,
                                   String exportFolder) {
        Objects.requireNonNull(dashboardSummary, "Dashboard summary must not be null.");
        Objects.requireNonNull(categorySummaries, "Category summaries must not be null.");
        Objects.requireNonNull(monthlySummaries, "Monthly summaries must not be null.");

        Path exportDir = createExportDirectory(exportFolder);

        String fileName = "summary_report_" + LocalDateTime.now().format(FILE_TIMESTAMP) + ".txt";
        Path filePath = exportDir.resolve(fileName);

        StringBuilder text = new StringBuilder();

        text.append("PERSONAL EXPENSE TRACKER SUMMARY REPORT").append(System.lineSeparator());
        text.append("Generated: ").append(LocalDateTime.now()).append(System.lineSeparator());
        text.append(System.lineSeparator());

        text.append("DASHBOARD SUMMARY").append(System.lineSeparator());
        text.append("Total Income: ").append(formatAmount(dashboardSummary.totalIncome())).append(System.lineSeparator());
        text.append("Total Expense: ").append(formatAmount(dashboardSummary.totalExpense())).append(System.lineSeparator());
        text.append("Balance: ").append(formatAmount(dashboardSummary.balance())).append(System.lineSeparator());
        text.append(System.lineSeparator());

        text.append("CATEGORY SUMMARIES").append(System.lineSeparator());
        if (categorySummaries.isEmpty()) {
            text.append("No category summaries available.").append(System.lineSeparator());
        } else {
            for (CategorySummary summary : categorySummaries) {
                text.append("- ")
                        .append(summary.categoryName())
                        .append(" | Total: ").append(formatAmount(summary.totalAmount()))
                        .append(" | Count: ").append(summary.transactionCount())
                        .append(" | Average: ").append(formatAmount(summary.getAverageAmount()))
                        .append(System.lineSeparator());
            }
        }
        text.append(System.lineSeparator());

        text.append("MONTHLY SUMMARIES").append(System.lineSeparator());
        if (monthlySummaries.isEmpty()) {
            text.append("No monthly summaries available.").append(System.lineSeparator());
        } else {
            for (MonthlySummary summary : monthlySummaries) {
                text.append("- ")
                        .append(summary.getMonthLabel())
                        .append(" | Income: ").append(formatAmount(summary.totalIncome()))
                        .append(" | Expense: ").append(formatAmount(summary.totalExpense()))
                        .append(" | Balance: ").append(formatAmount(summary.balance()))
                        .append(" | Performance: ").append(summary.getPerformanceLabel())
                        .append(System.lineSeparator());
            }
        }

        writeFile(filePath, text.toString());
        return filePath;
    }

    private Path createExportDirectory(String exportFolder) {
        String folder = (exportFolder == null || exportFolder.isBlank()) ? "exports" : exportFolder.trim();
        Path exportDir = Path.of(folder);

        try {
            Files.createDirectories(exportDir);
            return exportDir;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create export directory: " + exportDir, e);
        }
    }
    
    public void handleExportRequest(ExportRequest request) {
        switch (request) {
            case TransactionExportRequest txReq -> {
                List<Transaction> transactions = new TransactionService().getAllTransactionsSortedByDateDesc();
                exportTransactionsToCsv(transactions, txReq.getExportFolder());
            }
            case SummaryExportRequest summaryReq -> {
                TransactionService transactionService = new TransactionService();
                ReportService reportService = new ReportService();

                exportSummaryToTxt(
                        transactionService.calculateDashboardSummary(),
                        reportService.getCategorySummaries(),
                        reportService.getMonthlySummaries(),
                        summaryReq.getExportFolder()
                );
            }
        }
    }

    private void writeFile(Path filePath, String content) {
        try {
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write export file: " + filePath, e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "0" : amount.toPlainString();
    }
}