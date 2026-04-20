package com.taha.expensetracker.model;

import com.taha.expensetracker.model.enums.TransactionType;

import java.util.Objects;

public final class Category {
    private Long id;
    private String name;
    private TransactionType type;

    public Category() {
    }

    public Category(Long id, String name, TransactionType type) {
        setId(id);
        setName(name);
        setType(type);
    }

    public Category(String name, TransactionType type) {
        this(null, name, type);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Category id must be greater than zero.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String cleanedName = Objects.requireNonNull(name, "Category name must not be null.").trim();
        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException("Category name must not be empty.");
        }
        if (cleanedName.length() > 100) {
            throw new IllegalArgumentException("Category name must not exceed 100 characters.");
        }
        this.name = cleanedName;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "Transaction type must not be null.");
    }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category category)) {
            return false;
        }
        return Objects.equals(id, category.id)
                && Objects.equals(name, category.name)
                && type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}