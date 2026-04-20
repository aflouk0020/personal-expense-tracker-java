package com.taha.expensetracker.ui.panel;

import com.taha.expensetracker.dao.AppSettingsDao;
import com.taha.expensetracker.model.AppSettings;
import com.taha.expensetracker.model.Category;
import com.taha.expensetracker.model.Transaction;
import com.taha.expensetracker.model.enums.TransactionType;
import com.taha.expensetracker.service.CategoryService;
import com.taha.expensetracker.service.ExportService;
import com.taha.expensetracker.service.LocalizationService;
import com.taha.expensetracker.service.TransactionService;
import com.taha.expensetracker.ui.UITheme;
import com.taha.expensetracker.util.CurrencyFormatUtil;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TransactionsPanel extends JPanel {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final LocalizationService localizationService;
    private final ExportService exportService;
    private final AppSettingsDao appSettingsDao;

    private final DefaultTableModel tableModel;
    private final JTable transactionTable;
    private final JLabel statusLabel;

    public TransactionsPanel(LocalizationService localizationService) {
        this(new TransactionService(), new CategoryService(), new ExportService(), new AppSettingsDao(), localizationService);
    }

    public TransactionsPanel(TransactionService transactionService,
                             CategoryService categoryService,
                             ExportService exportService,
                             AppSettingsDao appSettingsDao,
                             LocalizationService localizationService) {
        this.transactionService = Objects.requireNonNull(transactionService, "TransactionService must not be null.");
        this.categoryService = Objects.requireNonNull(categoryService, "CategoryService must not be null.");
        this.exportService = Objects.requireNonNull(exportService, "ExportService must not be null.");
        this.appSettingsDao = Objects.requireNonNull(appSettingsDao, "AppSettingsDao must not be null.");
        this.localizationService = Objects.requireNonNull(localizationService, "LocalizationService must not be null.");

        setLayout(new BorderLayout(16, 16));
        UITheme.stylePage(this);

        JLabel titleLabel = new JLabel(localizationService.get("nav.transactions"));
        UITheme.styleTitle(titleLabel);

        JLabel subtitleLabel = new JLabel("Manage every income and expense from one premium workspace.");
        UITheme.styleSubtitle(subtitleLabel);

        JButton addButton = new JButton(localizationService.get("button.add"));
        JButton deleteButton = new JButton(localizationService.get("button.delete"));
        JButton exportButton = new JButton("Export CSV");
        JButton refreshButton = new JButton(localizationService.get("button.refresh"));

        UITheme.stylePrimaryButton(addButton);
        UITheme.styleSecondaryButton(deleteButton);
        UITheme.styleSecondaryButton(exportButton);
        UITheme.styleSecondaryButton(refreshButton);

        addButton.addActionListener(e -> showAddDialog());
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        exportButton.addActionListener(e -> exportTransactions());
        refreshButton.addActionListener(e -> loadData());

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(titleLabel);
        titleBlock.add(javax.swing.Box.createVerticalStrut(4));
        titleBlock.add(subtitleLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(addButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(exportButton);
        actionsPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        UITheme.styleCard(topPanel);
        topPanel.add(titleBlock, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        localizationService.get("label.title"),
                        localizationService.get("label.amount"),
                        localizationService.get("label.type"),
                        localizationService.get("label.category"),
                        localizationService.get("label.date"),
                        localizationService.get("label.notes")
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        UITheme.styleTable(transactionTable, scrollPane);

        JPanel tableCard = new JPanel(new BorderLayout());
        UITheme.styleCard(tableCard);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                UITheme.createCardBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        UITheme.styleStatus(statusLabel);

        add(topPanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactionsSortedByDateDesc();
            AppSettings settings = getCurrentSettings();

            tableModel.setRowCount(0);
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getTitle(),
                        CurrencyFormatUtil.format(t.getAmount(), settings),
                        t.getType().getDisplayName(),
                        t.getCategory().getName(),
                        t.getTransactionDate(),
                        t.getNotes()
                });
            }

            UITheme.styleSuccessStatus(statusLabel, "Loaded " + transactions.size() + " transaction(s).");
        } catch (Exception e) {
            tableModel.setRowCount(0);
            UITheme.styleErrorStatus(statusLabel, "Error loading transactions: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        List<Category> allCategories = categoryService.getAllCategories();

        if (allCategories.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please add at least one category first.",
                    "No Categories Available",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JTextField titleField = new JTextField();
        JTextField amountField = new JTextField();
        JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
        JComboBox<Category> categoryBox = new JComboBox<>();
        JLabel categoryHintLabel = new JLabel(" ");
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextArea notesArea = new JTextArea(4, 20);

        UITheme.styleField(titleField);
        UITheme.styleField(amountField);
        UITheme.styleField(typeBox);
        UITheme.styleField(categoryBox);
        UITheme.styleField(dateField);
        UITheme.styleTextArea(notesArea);
        UITheme.styleSubtitle(categoryHintLabel);

        typeBox.setSelectedItem(TransactionType.EXPENSE);
        updateCategoryBoxAndHint(categoryBox, categoryHintLabel, TransactionType.EXPENSE);

        typeBox.addActionListener(e -> {
            TransactionType selectedType = (TransactionType) typeBox.getSelectedItem();
            updateCategoryBoxAndHint(categoryBox, categoryHintLabel, selectedType);
        });

        JPanel panel = new JPanel(new GridLayout(7, 2, 12, 12));
        panel.setBackground(UITheme.BG);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);
        panel.add(new JLabel(""));
        panel.add(categoryHintLabel);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Transaction",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String title = titleField.getText().trim();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            TransactionType type = (TransactionType) typeBox.getSelectedItem();
            Category category = (Category) categoryBox.getSelectedItem();
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String notes = notesArea.getText().trim();

            if (category == null) {
                throw new IllegalArgumentException(
                        "No category is available for the selected type. Please add one first."
                );
            }

            Transaction transaction = new Transaction(title, amount, type, category, date, notes);
            transactionService.saveTransaction(transaction);

            loadData();
            UITheme.styleSuccessStatus(statusLabel, "Transaction added successfully.");
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error adding transaction: " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Add Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a transaction first.",
                    "No Transaction Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 0);
        long transactionId = ((Number) idValue).longValue();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected transaction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = transactionService.deleteTransaction(transactionId);

            if (deleted) {
                loadData();
                UITheme.styleSuccessStatus(statusLabel, "Transaction deleted successfully.");
            } else {
                UITheme.styleErrorStatus(statusLabel, "Transaction could not be deleted.");
            }
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error deleting transaction: " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Delete Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactionsSortedByDateDesc();
            AppSettings settings = getCurrentSettings();

            Path exportedFile = exportService.exportTransactionsToCsv(transactions, settings.getExportFolder());

            UITheme.styleSuccessStatus(statusLabel, "Transactions exported to: " + exportedFile);
            JOptionPane.showMessageDialog(
                    this,
                    "Transactions exported successfully:\n" + exportedFile,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error exporting transactions: " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCategoryBoxAndHint(JComboBox<Category> categoryBox, JLabel categoryHintLabel, TransactionType type) {
        categoryBox.removeAllItems();

        if (type == null) {
            categoryHintLabel.setText(" ");
            return;
        }

        List<Category> categories = categoryService.getCategoriesByType(type);
        for (Category category : categories) {
            categoryBox.addItem(category);
        }

        if (categories.isEmpty()) {
            categoryHintLabel.setText("No " + type.getDisplayName() + " categories available.");
        } else {
            categoryHintLabel.setText(categories.size() + " matching categor" +
                    (categories.size() == 1 ? "y" : "ies") + " found.");
        }
    }

    private AppSettings getCurrentSettings() {
        return appSettingsDao.loadSettings()
                .orElse(new AppSettings("en", "IE", "EUR", "exports"));
    }
}