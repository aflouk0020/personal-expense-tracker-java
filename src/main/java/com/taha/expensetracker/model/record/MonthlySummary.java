package com.taha.expensetracker.model.record;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

public record MonthlySummary(
        YearMonth month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {
    public MonthlySummary {
        month = Objects.requireNonNull(month, "Month must not be null.");

        totalIncome = normalise(totalIncome, "Total income must not be null.");
        totalExpense = normalise(totalExpense, "Total expense must not be null.");
        balance = normalise(balance, "Balance must not be null.");
    }

    private static BigDecimal normalise(BigDecimal value, String message) {
        return Objects.requireNonNull(value, message).stripTrailingZeros();
    }

    public boolean isProfitable() {
        return balance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isLoss() {
        return balance.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isBreakEven() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public String getMonthLabel() {
        return month.getMonth().name() + " " + month.getYear();
    }

    public String getPerformanceLabel() {
        return switch (balance.signum()) {
            case -1 -> "Loss";
            case 0 -> "Break-even";
            case 1 -> "Profit";
            default -> "Unknown";
        };
    }
}