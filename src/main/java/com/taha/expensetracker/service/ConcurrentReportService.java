package com.taha.expensetracker.service;

import com.taha.expensetracker.model.record.CategorySummary;
import com.taha.expensetracker.model.record.ConcurrentReportResult;
import com.taha.expensetracker.model.record.DashboardSummary;
import com.taha.expensetracker.model.record.MonthlySummary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentReportService {

    private final TransactionService transactionService;
    private final ReportService reportService;

    public ConcurrentReportService() {
        this(new TransactionService(), new ReportService());
    }

    public ConcurrentReportService(TransactionService transactionService, ReportService reportService) {
        this.transactionService = Objects.requireNonNull(transactionService, "TransactionService must not be null.");
        this.reportService = Objects.requireNonNull(reportService, "ReportService must not be null.");
    }

    public ConcurrentReportResult generateAllReports() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            Callable<DashboardSummary> dashboardTask = transactionService::calculateDashboardSummary;
            Callable<List<CategorySummary>> categoryTask = reportService::getCategorySummaries;
            Callable<List<MonthlySummary>> monthlyTask = reportService::getMonthlySummaries;

            Future<DashboardSummary> dashboardFuture = executorService.submit(dashboardTask);
            Future<List<CategorySummary>> categoryFuture = executorService.submit(categoryTask);
            Future<List<MonthlySummary>> monthlyFuture = executorService.submit(monthlyTask);

            DashboardSummary dashboardSummary = dashboardFuture.get();
            List<CategorySummary> categorySummaries = categoryFuture.get();
            List<MonthlySummary> monthlySummaries = monthlyFuture.get();

            return new ConcurrentReportResult(dashboardSummary, categorySummaries, monthlySummaries);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Concurrent report generation was interrupted.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error generating reports concurrently.", e);
        } finally {
            executorService.shutdown();
        }
    }

    public Map<String, Object> generateReportMap() {
        ConcurrentReportResult result = generateAllReports();

        Map<String, Object> reportMap = new LinkedHashMap<>();
        reportMap.put("dashboardSummary", result.dashboardSummary());
        reportMap.put("categorySummaries", result.categorySummaries());
        reportMap.put("monthlySummaries", result.monthlySummaries());

        return reportMap;
    }
}