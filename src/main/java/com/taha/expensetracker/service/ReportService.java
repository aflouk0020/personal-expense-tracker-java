package com.taha.expensetracker.service;

import com.taha.expensetracker.dao.TransactionDao;
import com.taha.expensetracker.model.Transaction;
import com.taha.expensetracker.model.record.CategorySummary;
import com.taha.expensetracker.model.record.MonthlySummary;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportService {

    private final TransactionDao transactionDao;

    public ReportService() {
        this(new TransactionDao());
    }

    public ReportService(TransactionDao transactionDao) {
        this.transactionDao = Objects.requireNonNull(transactionDao, "TransactionDao must not be null.");
    }

    public List<CategorySummary> getCategorySummaries() {
        Map<String, List<Transaction>> groupedByCategory = transactionDao.findAll()
                .stream()
                .filter(Transaction::isExpense)
                .collect(Collectors.groupingBy(transaction -> transaction.getCategory().getName()));

        return groupedByCategory.entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue()
                            .stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    long count = entry.getValue().size();

                    return new CategorySummary(entry.getKey(), total, count);
                })
                .sorted(Comparator.comparing(CategorySummary::totalAmount).reversed())
                .toList();
    }

    public List<MonthlySummary> getMonthlySummaries() {
        Map<YearMonth, List<Transaction>> groupedByMonth = transactionDao.findAll()
                .stream()
                .collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getTransactionDate())));

        return groupedByMonth.entrySet()
                .stream()
                .map(entry -> {
                    BigDecimal income = entry.getValue()
                            .stream()
                            .filter(Transaction::isIncome)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal expense = entry.getValue()
                            .stream()
                            .filter(Transaction::isExpense)
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal balance = income.subtract(expense);

                    return new MonthlySummary(entry.getKey(), income, expense, balance);
                })
                .sorted(Comparator.comparing(MonthlySummary::month).reversed())
                .toList();
    }

    public Map<Boolean, List<Transaction>> partitionTransactionsByIncome() {
        return transactionDao.findAll()
                .stream()
                .collect(Collectors.partitioningBy(Transaction::isIncome));
    }

    public Map<Long, String> getTransactionTitleMap() {
        return transactionDao.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Transaction::getId,
                        Transaction::getTitle,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Long> countTransactionsPerCategory() {
        return transactionDao.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getCategory().getName(),
                        Collectors.counting()
                ));
    }

    public void printAllTransactions() {
        transactionDao.findAll()
                .stream()
                .forEach(System.out::println);
    }
}