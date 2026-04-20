package com.taha.expensetracker.service;

import com.taha.expensetracker.dao.TransactionDao;
import com.taha.expensetracker.model.Transaction;
import com.taha.expensetracker.model.record.DashboardSummary;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class TransactionService {

    private final TransactionDao transactionDao;

    public TransactionService() {
        this(new TransactionDao());
    }

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = Objects.requireNonNull(transactionDao, "TransactionDao must not be null.");
    }

    public List<Transaction> getAllTransactionsSortedByDateDesc() {
        return transactionDao.findAll()
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed()
                        .thenComparing(Transaction::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<Transaction> filterTransactions(Predicate<Transaction> filter) {
        return transactionDao.findAll()
                .stream()
                .filter(filter)
                .toList();
    }

    public List<Transaction> getIncomeTransactions() {
        return filterTransactions(Transaction::isIncome);
    }

    public List<Transaction> getExpenseTransactions() {
        return filterTransactions(Transaction::isExpense);
    }

    public Transaction saveTransaction(Transaction transaction) {
        validateTransaction(transaction);
        return transactionDao.save(transaction);
    }

    public boolean deleteTransaction(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Transaction id must be greater than zero.");
        }
        return transactionDao.deleteById(id);
    }

    public DashboardSummary calculateDashboardSummary() {
        List<Transaction> transactions = transactionDao.findAll();

        Function<Transaction, BigDecimal> amountMapper = Transaction::getAmount;

        BigDecimal totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .map(amountMapper)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(Transaction::isExpense)
                .map(amountMapper)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new DashboardSummary(totalIncome, totalExpense, balance);
    }

    public BigDecimal getMaxTransactionAmount() {
        return transactionDao.findAll()
                .stream()
                .map(Transaction::getAmount)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    public long countTransactions() {
        return transactionDao.findAll()
                .stream()
                .count();
    }

    public boolean hasAnyLargeTransaction(BigDecimal threshold) {
        return transactionDao.findAll()
                .stream()
                .anyMatch(t -> t.getAmount().compareTo(threshold) > 0);
    }

    public boolean allTransactionsPositive() {
        return transactionDao.findAll()
                .stream()
                .allMatch(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    public boolean noTransactions() {
        return transactionDao.findAll()
                .stream()
                .noneMatch(t -> true);
    }

    public Transaction findFirstTransaction() {
        return transactionDao.findAll()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public Transaction findAnyTransaction() {
        return transactionDao.findAll()
                .stream()
                .findAny()
                .orElse(null);
    }

    public List<Transaction> getTopNTransactions(int n) {
        return transactionDao.findAll()
                .stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(n)
                .toList();
    }

    public List<Transaction> getDistinctTransactionsByTitle() {
        return transactionDao.findAll()
                .stream()
                .distinct()
                .toList();
    }

    private void validateTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction must not be null.");

        if (transaction.getTitle() == null || transaction.getTitle().isBlank()) {
            throw new IllegalArgumentException("Transaction title must not be empty.");
        }

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type must not be null.");
        }

        if (transaction.getCategory() == null) {
            throw new IllegalArgumentException("Category must not be null.");
        }

        if (transaction.getCategory().getId() == null) {
            throw new IllegalArgumentException("Category id must not be null.");
        }

        if (transaction.getCategory().getType() != transaction.getType()) {
            throw new IllegalArgumentException("Category type must match transaction type.");
        }

        if (transaction.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction date must not be null.");
        }
    }
}