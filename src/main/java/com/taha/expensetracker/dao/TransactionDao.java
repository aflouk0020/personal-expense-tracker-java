package com.taha.expensetracker.dao;

import com.taha.expensetracker.model.Category;
import com.taha.expensetracker.model.Transaction;
import com.taha.expensetracker.model.enums.TransactionType;
import com.taha.expensetracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDao {

    public List<Transaction> findAll() {
        String sql = """
                SELECT t.id,
                       t.title,
                       t.amount,
                       t.type,
                       t.category_id,
                       t.transaction_date,
                       t.notes,
                       t.created_at,
                       t.updated_at,
                       c.name AS category_name,
                       c.type AS category_type
                FROM transactions t
                JOIN categories c ON t.category_id = c.id
                ORDER BY t.transaction_date DESC, t.id DESC
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions.", e);
        }

        return transactions;
    }

    public Optional<Transaction> findById(long id) {
        String sql = """
                SELECT t.id,
                       t.title,
                       t.amount,
                       t.type,
                       t.category_id,
                       t.transaction_date,
                       t.notes,
                       t.created_at,
                       t.updated_at,
                       c.name AS category_name,
                       c.type AS category_type
                FROM transactions t
                JOIN categories c ON t.category_id = c.id
                WHERE t.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding transaction by id.", e);
        }

        return Optional.empty();
    }

    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            return insert(transaction);
        }
        return update(transaction);
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting transaction.", e);
        }
    }

    private Transaction insert(Transaction transaction) {
        String sql = """
                INSERT INTO transactions
                (title, amount, type, category_id, transaction_date, notes)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, transaction);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setId(keys.getLong(1));
                }
            }

            return transaction;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting transaction.", e);
        }
    }

    private Transaction update(Transaction transaction) {
        String sql = """
                UPDATE transactions
                SET title = ?,
                    amount = ?,
                    type = ?,
                    category_id = ?,
                    transaction_date = ?,
                    notes = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, transaction);
            ps.setLong(7, transaction.getId());
            ps.executeUpdate();

            return transaction;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating transaction.", e);
        }
    }

    private void fillStatement(PreparedStatement ps, Transaction transaction) throws SQLException {
        ps.setString(1, transaction.getTitle());
        ps.setBigDecimal(2, transaction.getAmount());
        ps.setString(3, transaction.getType().name());
        ps.setLong(4, transaction.getCategory().getId());
        ps.setDate(5, Date.valueOf(transaction.getTransactionDate()));

        if (transaction.getNotes() == null) {
            ps.setString(6, null);
        } else {
            ps.setString(6, transaction.getNotes());
        }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        TransactionType transactionType = TransactionType.valueOf(rs.getString("type"));
        TransactionType categoryType = TransactionType.valueOf(rs.getString("category_type"));

        Category category = new Category(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                categoryType
        );

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new Transaction(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getBigDecimal("amount"),
                transactionType,
                category,
                rs.getDate("transaction_date").toLocalDate(),
                rs.getString("notes"),
                createdAt == null ? null : createdAt.toLocalDateTime(),
                updatedAt == null ? null : updatedAt.toLocalDateTime()
        );
    }
}