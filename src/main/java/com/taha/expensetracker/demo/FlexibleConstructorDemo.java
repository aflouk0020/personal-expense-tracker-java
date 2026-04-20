package com.taha.expensetracker.demo;

public class FlexibleConstructorDemo {

    private final String name;
    private final double amount;

    public FlexibleConstructorDemo(String name, double amount) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.name = name;
        this.amount = amount;

        System.out.println("Flexible constructor executed successfully");
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
}