package com.taha.expensetracker.model.record;

import java.math.BigDecimal;
import java.util.Objects;

public record CategorySummary(
        String categoryName,
        BigDecimal totalAmount,
        long transactionCount
) {
    public CategorySummary {
        categoryName = Objects.requireNonNull(categoryName, "Category name must not be null.").trim();
        if (categoryName.isEmpty()) {
            throw new IllegalArgumentException("Category name must not be empty.");
        }

        totalAmount = Objects.requireNonNull(totalAmount, "Total amount must not be null.").stripTrailingZeros();

        if (transactionCount < 0) {
            throw new IllegalArgumentException("Transaction count must not be negative.");
        }
    }

    public boolean hasTransactions() {
        return transactionCount > 0;
    }

    public BigDecimal getAverageAmount() {
        if (transactionCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(BigDecimal.valueOf(transactionCount), 2, java.math.RoundingMode.HALF_UP);
    }
}