package com.taha.expensetracker.ui.panel;

import com.taha.expensetracker.dao.AppSettingsDao;
import com.taha.expensetracker.model.AppSettings;
import com.taha.expensetracker.service.LocalizationService;
import com.taha.expensetracker.ui.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import java.util.Optional;

public class SettingsPanel extends JPanel {

    private final AppSettingsDao appSettingsDao;
    private final LocalizationService localizationService;
    private final Runnable onSettingsSaved;

    private final JComboBox<String> languageCodeComboBox;
    private final JComboBox<String> countryCodeComboBox;
    private final JComboBox<String> currencyCodeComboBox;
    private final JTextField exportFolderField;
    private final JLabel statusLabel;

    public SettingsPanel(LocalizationService localizationService, Runnable onSettingsSaved) {
        this(new AppSettingsDao(), localizationService, onSettingsSaved);
    }

    public SettingsPanel(AppSettingsDao appSettingsDao,
                         LocalizationService localizationService,
                         Runnable onSettingsSaved) {
        this.appSettingsDao = Objects.requireNonNull(appSettingsDao, "AppSettingsDao must not be null.");
        this.localizationService = Objects.requireNonNull(localizationService, "LocalizationService must not be null.");
        this.onSettingsSaved = Objects.requireNonNull(onSettingsSaved, "onSettingsSaved callback must not be null.");

        setLayout(new BorderLayout(16, 16));
        UITheme.stylePage(this);

        JLabel titleLabel = new JLabel(localizationService.get("nav.settings"));
        UITheme.styleTitle(titleLabel);

        JLabel subtitleLabel = new JLabel("Manage language, country, currency, and export preferences.");
        UITheme.styleSubtitle(subtitleLabel);

        JButton loadButton = new JButton(localizationService.get("button.refresh"));
        loadButton.addActionListener(e -> loadSettings());
        UITheme.styleSecondaryButton(loadButton);

        JButton saveButton = new JButton(localizationService.get("button.save"));
        saveButton.addActionListener(e -> saveSettings());
        UITheme.stylePrimaryButton(saveButton);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(titleLabel);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(loadButton);
        actionsPanel.add(saveButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        UITheme.styleCard(topPanel);
        topPanel.add(titleBlock, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        UITheme.styleCard(formPanel);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Application Settings");
        titledBorder.setTitleFont(UITheme.FONT_H3);
        titledBorder.setTitleColor(UITheme.TEXT);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                UITheme.createCardBorder(),
                BorderFactory.createCompoundBorder(
                        titledBorder,
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        languageCodeComboBox = new JComboBox<>(new String[]{"en", "fr"});
        countryCodeComboBox = new JComboBox<>(new String[]{"IE", "FR", "US", "GB"});
        currencyCodeComboBox = new JComboBox<>(new String[]{"EUR", "USD", "GBP"});
        exportFolderField = new JTextField(20);

        UITheme.styleField(languageCodeComboBox);
        UITheme.styleField(countryCodeComboBox);
        UITheme.styleField(currencyCodeComboBox);
        UITheme.styleField(exportFolderField);

        addFormRow(formPanel, gbc, 0, "Language Code", languageCodeComboBox);
        addFormRow(formPanel, gbc, 1, "Country Code", countryCodeComboBox);
        addFormRow(formPanel, gbc, 2, "Currency Code", currencyCodeComboBox);
        addFormRow(formPanel, gbc, 3, "Export Folder", exportFolderField);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        UITheme.styleStatus(statusLabel);

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadSettings();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component component) {
        JLabel label = new JLabel(labelText + ":");
        label.setFont(UITheme.FONT_H3);
        label.setForeground(UITheme.TEXT);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    public void loadSettings() {
        try {
            Optional<AppSettings> optionalSettings = appSettingsDao.loadSettings();

            if (optionalSettings.isPresent()) {
                AppSettings settings = optionalSettings.get();
                languageCodeComboBox.setSelectedItem(settings.getLanguageCode());
                countryCodeComboBox.setSelectedItem(settings.getCountryCode());
                currencyCodeComboBox.setSelectedItem(settings.getCurrencyCode());
                exportFolderField.setText(settings.getExportFolder());
                UITheme.styleSuccessStatus(statusLabel, "Settings loaded successfully.");
            } else {
                UITheme.styleErrorStatus(statusLabel, "No settings found in database.");
            }
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error loading settings: " + e.getMessage());
        }
    }

    public void saveSettings() {
        try {
            AppSettings settings = new AppSettings(
                    Objects.toString(languageCodeComboBox.getSelectedItem(), "en"),
                    Objects.toString(countryCodeComboBox.getSelectedItem(), "IE"),
                    Objects.toString(currencyCodeComboBox.getSelectedItem(), "EUR"),
                    exportFolderField.getText().trim()
            );

            appSettingsDao.saveSettings(settings);
            UITheme.styleSuccessStatus(statusLabel, "Settings saved successfully.");

            JOptionPane.showMessageDialog(
                    this,
                    "Settings saved successfully.\nThe application will now refresh.",
                    "Settings Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            onSettingsSaved.run();

        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error saving settings: " + e.getMessage());
        }
    }
}