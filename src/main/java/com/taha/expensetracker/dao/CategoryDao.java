package com.taha.expensetracker.dao;

import com.taha.expensetracker.model.Category;
import com.taha.expensetracker.model.enums.TransactionType;
import com.taha.expensetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDao {

    public List<Category> findAll() {
        String sql = "SELECT id, name, type FROM categories ORDER BY name";

        List<Category> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching categories", e);
        }

        return categories;
    }

    public Optional<Category> findById(long id) {
        String sql = "SELECT id, name, type FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by id", e);
        }

        return Optional.empty();
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            return insert(category);
        } else {
            return update(category);
        }
    }

    private Category insert(Category category) {
        String sql = "INSERT INTO categories (name, type) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getType().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setId(keys.getLong(1));
                }
            }

            return category;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting category", e);
        }
    }

    private Category update(Category category) {
        String sql = "UPDATE categories SET name = ?, type = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getType().name());
            ps.setLong(3, category.getId());

            ps.executeUpdate();

            return category;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String typeStr = rs.getString("type");

        TransactionType type = TransactionType.valueOf(typeStr);

        return new Category(id, name, type);
    }
}