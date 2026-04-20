package com.taha.expensetracker.model.record;

import java.math.BigDecimal;
import java.util.Objects;

public record DashboardSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {
    public DashboardSummary {
        totalIncome = normalise(totalIncome, "Total income must not be null.");
        totalExpense = normalise(totalExpense, "Total expense must not be null.");
        balance = normalise(balance, "Balance must not be null.");
    }

    private static BigDecimal normalise(BigDecimal value, String message) {
        return Objects.requireNonNull(value, message).stripTrailingZeros();
    }

    public static DashboardSummary empty() {
        return new DashboardSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public boolean hasIncome() {
        return totalIncome.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasExpense() {
        return totalExpense.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isBalancedOrPositive() {
        return balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    public String getBalanceStatus() {
        return switch (balance.signum()) {
            case -1 -> "Negative";
            case 0 -> "Balanced";
            case 1 -> "Positive";
            default -> "Unknown";
        };
    }
}