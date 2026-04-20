package com.taha.expensetracker.ui.panel;

import com.taha.expensetracker.model.Category;
import com.taha.expensetracker.model.enums.TransactionType;
import com.taha.expensetracker.service.CategoryService;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

public class CategoriesPanel extends JPanel {

    private final CategoryService categoryService;
    private final LocalizationService localizationService;

    private final DefaultTableModel tableModel;
    private final JTable categoryTable;
    private final JLabel statusLabel;

    public CategoriesPanel(LocalizationService localizationService) {
        this(new CategoryService(), localizationService);
    }

    public CategoriesPanel(CategoryService categoryService, LocalizationService localizationService) {
        this.categoryService = Objects.requireNonNull(categoryService, "CategoryService must not be null.");
        this.localizationService = Objects.requireNonNull(localizationService, "LocalizationService must not be null.");

        setLayout(new BorderLayout(16, 16));
        UITheme.stylePage(this);

        JLabel titleLabel = new JLabel(localizationService.get("nav.categories"));
        UITheme.styleTitle(titleLabel);

        JLabel subtitleLabel = new JLabel("Organise your income and expense categories in one clean place.");
        UITheme.styleSubtitle(subtitleLabel);

        JButton addButton = new JButton(localizationService.get("button.add"));
        addButton.addActionListener(e -> showAddDialog());
        UITheme.stylePrimaryButton(addButton);

        JButton deleteButton = new JButton(localizationService.get("button.delete"));
        deleteButton.addActionListener(e -> deleteSelectedCategory());
        UITheme.styleSecondaryButton(deleteButton);

        JButton refreshButton = new JButton(localizationService.get("button.refresh"));
        refreshButton.addActionListener(e -> loadData());
        UITheme.styleSecondaryButton(refreshButton);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(titleLabel);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(addButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        UITheme.styleCard(topPanel);
        topPanel.add(titleBlock, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", localizationService.get("label.type")}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        UITheme.styleTable(categoryTable, scrollPane);

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
            List<Category> categories = categoryService.getAllCategories();

            tableModel.setRowCount(0);
            for (Category c : categories) {
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getName(),
                        c.getType().getDisplayName()
                });
            }

            UITheme.styleSuccessStatus(
                    statusLabel,
                    "Loaded " + categories.size() + " categor" + (categories.size() == 1 ? "y." : "ies.")
            );
        } catch (Exception e) {
            tableModel.setRowCount(0);
            UITheme.styleErrorStatus(statusLabel, "Error loading categories: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField();
        JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());

        UITheme.styleField(nameField);
        UITheme.styleField(typeBox);

        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBackground(UITheme.BG);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Category",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            TransactionType type = (TransactionType) typeBox.getSelectedItem();

            Category category = new Category(name, type);
            categoryService.saveCategory(category);

            loadData();
            UITheme.styleSuccessStatus(statusLabel, "Category added successfully.");
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error adding category: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Add Category Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a category first.",
                    "No Category Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 0);
        long categoryId = ((Number) idValue).longValue();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected category?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = categoryService.deleteCategory(categoryId);

            if (deleted) {
                loadData();
                UITheme.styleSuccessStatus(statusLabel, "Category deleted successfully.");
            } else {
                UITheme.styleErrorStatus(statusLabel, "Category could not be deleted.");
            }
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error deleting category: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "This category may still be used by one or more transactions.\n\n" + e.getMessage(),
                    "Delete Category Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}