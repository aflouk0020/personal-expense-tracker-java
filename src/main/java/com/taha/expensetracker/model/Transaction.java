package com.taha.expensetracker.model;

import com.taha.expensetracker.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Transaction {
    private Long id;
    private String title;
    private BigDecimal amount;
    private TransactionType type;
    private Category category;
    private LocalDate transactionDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transaction() {
    }

    public Transaction(Long id,
                       String title,
                       BigDecimal amount,
                       TransactionType type,
                       Category category,
                       LocalDate transactionDate,
                       String notes,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
        setId(id);
        setTitle(title);
        setAmount(amount);
        setType(type);
        setCategory(category);
        setTransactionDate(transactionDate);
        setNotes(notes);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    public Transaction(String title,
                       BigDecimal amount,
                       TransactionType type,
                       Category category,
                       LocalDate transactionDate,
                       String notes) {
        this(null, title, amount, type, category, transactionDate, notes, null, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Transaction id must be greater than zero.");
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String cleanedTitle = Objects.requireNonNull(title, "Transaction title must not be null.").trim();
        if (cleanedTitle.isEmpty()) {
            throw new IllegalArgumentException("Transaction title must not be empty.");
        }
        if (cleanedTitle.length() > 150) {
            throw new IllegalArgumentException("Transaction title must not exceed 150 characters.");
        }
        this.title = cleanedTitle;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        BigDecimal safeAmount = Objects.requireNonNull(amount, "Amount must not be null.");
        if (safeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        this.amount = safeAmount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "Transaction type must not be null.");
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = Objects.requireNonNull(category, "Category must not be null.");
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = Objects.requireNonNull(transactionDate, "Transaction date must not be null.");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes == null) {
            this.notes = null;
            return;
        }

        String cleanedNotes = notes.trim();
        if (cleanedNotes.length() > 255) {
            throw new IllegalArgumentException("Notes must not exceed 255 characters.");
        }
        this.notes = cleanedNotes.isEmpty() ? null : cleanedNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public String getDisplayAmount() {
        return switch (type) {
            case INCOME -> "+" + amount;
            case EXPENSE -> "-" + amount;
        };
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", category=" + category +
                ", transactionDate=" + transactionDate +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(amount, that.amount)
                && type == that.type
                && Objects.equals(category, that.category)
                && Objects.equals(transactionDate, that.transactionDate)
                && Objects.equals(notes, that.notes)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, amount, type, category, transactionDate, notes, createdAt, updatedAt);
    }
}