package com.taha.expensetracker.model.record;

import java.util.List;
import java.util.Objects;

public record ConcurrentReportResult(
        DashboardSummary dashboardSummary,
        List<CategorySummary> categorySummaries,
        List<MonthlySummary> monthlySummaries
) {
    public ConcurrentReportResult {
        dashboardSummary = Objects.requireNonNull(dashboardSummary, "Dashboard summary must not be null.");
        categorySummaries = List.copyOf(Objects.requireNonNull(categorySummaries, "Category summaries must not be null."));
        monthlySummaries = List.copyOf(Objects.requireNonNull(monthlySummaries, "Monthly summaries must not be null."));
    }
}