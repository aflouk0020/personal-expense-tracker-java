package com.taha.expensetracker.dao;

import com.taha.expensetracker.model.AppSettings;
import com.taha.expensetracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AppSettingsDao {

    public Optional<AppSettings> loadSettings() {
        String sql = "SELECT language_code, country_code, currency_code, export_folder FROM app_settings LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                AppSettings settings = new AppSettings(
                        rs.getString("language_code"),
                        rs.getString("country_code"),
                        rs.getString("currency_code"),
                        rs.getString("export_folder")
                );
                return Optional.of(settings);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading application settings", e);
        }

        return Optional.empty();
    }

    public void saveSettings(AppSettings settings) {
        String sql = """
                UPDATE app_settings
                SET language_code = ?,
                    country_code = ?,
                    currency_code = ?,
                    export_folder = ?
                WHERE id = 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settings.getLanguageCode());
            ps.setString(2, settings.getCountryCode());
            ps.setString(3, settings.getCurrencyCode());
            ps.setString(4, settings.getExportFolder());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving application settings", e);
        }
    }
}