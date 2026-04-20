package com.taha.expensetracker.model.enums;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public String getDisplayName() {
        return switch (this) {
            case INCOME -> "Income";
            case EXPENSE -> "Expense";
        };
    }

    public boolean isIncome() {
        return this == INCOME;
    }

    public boolean isExpense() {
        return this == EXPENSE;
    }
}