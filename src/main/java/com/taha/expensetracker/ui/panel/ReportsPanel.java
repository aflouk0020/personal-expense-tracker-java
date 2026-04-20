package com.taha.expensetracker.ui.panel;

import com.taha.expensetracker.dao.AppSettingsDao;
import com.taha.expensetracker.model.AppSettings;
import com.taha.expensetracker.model.record.CategorySummary;
import com.taha.expensetracker.model.record.DashboardSummary;
import com.taha.expensetracker.model.record.MonthlySummary;
import com.taha.expensetracker.service.ExportService;
import com.taha.expensetracker.service.LocalizationService;
import com.taha.expensetracker.service.ReportService;
import com.taha.expensetracker.service.TransactionService;
import com.taha.expensetracker.ui.UITheme;
import com.taha.expensetracker.util.CurrencyFormatUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ReportsPanel extends JPanel {

    private final ReportService reportService;
    private final TransactionService transactionService;
    private final LocalizationService localizationService;
    private final ExportService exportService;
    private final AppSettingsDao appSettingsDao;

    private final JLabel totalIncomeLabel;
    private final JLabel totalExpenseLabel;
    private final JLabel balanceLabel;
    private final JLabel statusLabel;

    private final DefaultTableModel categoryTableModel;
    private final JTable categoryTable;

    private final DefaultTableModel monthlyTableModel;
    private final JTable monthlyTable;

    public ReportsPanel(LocalizationService localizationService) {
        this(new ReportService(), new TransactionService(), new ExportService(), new AppSettingsDao(), localizationService);
    }

    public ReportsPanel(ReportService reportService,
                        TransactionService transactionService,
                        ExportService exportService,
                        AppSettingsDao appSettingsDao,
                        LocalizationService localizationService) {
        this.reportService = Objects.requireNonNull(reportService, "ReportService must not be null.");
        this.transactionService = Objects.requireNonNull(transactionService, "TransactionService must not be null.");
        this.exportService = Objects.requireNonNull(exportService, "ExportService must not be null.");
        this.appSettingsDao = Objects.requireNonNull(appSettingsDao, "AppSettingsDao must not be null.");
        this.localizationService = Objects.requireNonNull(localizationService, "LocalizationService must not be null.");

        setLayout(new BorderLayout(16, 16));
        UITheme.stylePage(this);

        JLabel titleLabel = new JLabel(localizationService.get("nav.reports"));
        UITheme.styleTitle(titleLabel);

        JLabel subtitleLabel = new JLabel("Analyse your financial performance with a clean executive dashboard.");
        UITheme.styleSubtitle(subtitleLabel);

        JButton exportButton = new JButton("Export TXT");
        exportButton.addActionListener(e -> exportSummaryReport());
        UITheme.styleSecondaryButton(exportButton);

        JButton refreshButton = new JButton(localizationService.get("button.refresh"));
        refreshButton.addActionListener(e -> loadReports());
        UITheme.stylePrimaryButton(refreshButton);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(titleLabel);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitleLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(exportButton);
        actionsPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        UITheme.styleCard(topPanel);
        topPanel.add(titleBlock, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);

        totalIncomeLabel = new JLabel(CurrencyFormatUtil.format(BigDecimal.ZERO));
        totalExpenseLabel = new JLabel(CurrencyFormatUtil.format(BigDecimal.ZERO));
        balanceLabel = new JLabel(CurrencyFormatUtil.format(BigDecimal.ZERO));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 14, 14));
        summaryPanel.setOpaque(false);
        summaryPanel.add(UITheme.createSummaryCard(localizationService.get("label.totalIncome"), totalIncomeLabel));
        summaryPanel.add(UITheme.createSummaryCard(localizationService.get("label.totalExpense"), totalExpenseLabel));
        summaryPanel.add(UITheme.createSummaryCard(localizationService.get("label.balance"), balanceLabel));

        JPanel northContainer = new JPanel();
        northContainer.setOpaque(false);
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.add(topPanel);
        northContainer.add(Box.createVerticalStrut(16));
        northContainer.add(summaryPanel);

        categoryTableModel = new DefaultTableModel(
                new String[]{"Category", "Total Amount", "Transaction Count", "Average Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(categoryTableModel);
        JScrollPane categoryScrollPane = new JScrollPane(categoryTable);
        UITheme.styleTable(categoryTable, categoryScrollPane);

        monthlyTableModel = new DefaultTableModel(
                new String[]{"Month", "Income", "Expense", "Balance", "Performance"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        monthlyTable = new JTable(monthlyTableModel);
        JScrollPane monthlyScrollPane = new JScrollPane(monthlyTable);
        UITheme.styleTable(monthlyTable, monthlyScrollPane);

        JPanel categoryPanel = createPremiumTablePanel("Category Summaries", categoryScrollPane);
        JPanel monthlyPanel = createPremiumTablePanel("Monthly Summaries", monthlyScrollPane);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 16, 16));
        tablesPanel.setOpaque(false);
        tablesPanel.add(categoryPanel);
        tablesPanel.add(monthlyPanel);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        UITheme.styleStatus(statusLabel);

        add(northContainer, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadReports();
    }

    public void loadReports() {
        try {
            loadDashboardSummary();
            loadCategorySummaries();
            loadMonthlySummaries();
            UITheme.styleSuccessStatus(statusLabel, "Reports loaded successfully.");
        } catch (Exception e) {
            clearTables();
            resetSummaryCards();
            UITheme.styleErrorStatus(statusLabel, "Error loading reports: " + e.getMessage());
        }
    }

    private void loadDashboardSummary() {
        DashboardSummary summary = transactionService.calculateDashboardSummary();
        AppSettings settings = getCurrentSettings();

        totalIncomeLabel.setText(CurrencyFormatUtil.format(summary.totalIncome(), settings));
        totalExpenseLabel.setText(CurrencyFormatUtil.format(summary.totalExpense(), settings));
        balanceLabel.setText(CurrencyFormatUtil.format(summary.balance(), settings));
    }

    private void loadCategorySummaries() {
        List<CategorySummary> summaries = reportService.getCategorySummaries();
        AppSettings settings = getCurrentSettings();

        categoryTableModel.setRowCount(0);

        for (CategorySummary summary : summaries) {
            categoryTableModel.addRow(new Object[]{
                    summary.categoryName(),
                    CurrencyFormatUtil.format(summary.totalAmount(), settings),
                    summary.transactionCount(),
                    CurrencyFormatUtil.format(summary.getAverageAmount(), settings)
            });
        }
    }

    private void loadMonthlySummaries() {
        List<MonthlySummary> summaries = reportService.getMonthlySummaries();
        AppSettings settings = getCurrentSettings();

        monthlyTableModel.setRowCount(0);

        for (MonthlySummary summary : summaries) {
            monthlyTableModel.addRow(new Object[]{
                    summary.getMonthLabel(),
                    CurrencyFormatUtil.format(summary.totalIncome(), settings),
                    CurrencyFormatUtil.format(summary.totalExpense(), settings),
                    CurrencyFormatUtil.format(summary.balance(), settings),
                    summary.getPerformanceLabel()
            });
        }
    }

    private void exportSummaryReport() {
        try {
            DashboardSummary dashboardSummary = transactionService.calculateDashboardSummary();
            List<CategorySummary> categorySummaries = reportService.getCategorySummaries();
            List<MonthlySummary> monthlySummaries = reportService.getMonthlySummaries();

            AppSettings settings = getCurrentSettings();

            Path exportedFile = exportService.exportSummaryToTxt(
                    dashboardSummary,
                    categorySummaries,
                    monthlySummaries,
                    settings.getExportFolder()
            );

            UITheme.styleSuccessStatus(statusLabel, "Summary exported to: " + exportedFile);
            JOptionPane.showMessageDialog(
                    this,
                    "Summary exported successfully:\n" + exportedFile,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            UITheme.styleErrorStatus(statusLabel, "Error exporting summary: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel createPremiumTablePanel(String title, JScrollPane scrollPane) {
        JPanel panel = new JPanel(new BorderLayout());
        UITheme.styleCard(panel);

        TitledBorder titledBorder = javax.swing.BorderFactory.createTitledBorder(title);
        titledBorder.setTitleFont(UITheme.FONT_H3);
        titledBorder.setTitleColor(UITheme.TEXT);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                UITheme.createCardBorder(),
                javax.swing.BorderFactory.createCompoundBorder(
                        titledBorder,
                        javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8)
                )
        ));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void clearTables() {
        categoryTableModel.setRowCount(0);
        monthlyTableModel.setRowCount(0);
    }

    private void resetSummaryCards() {
        totalIncomeLabel.setText(CurrencyFormatUtil.format(BigDecimal.ZERO));
        totalExpenseLabel.setText(CurrencyFormatUtil.format(BigDecimal.ZERO));
        balanceLabel.setText(CurrencyFormatUtil.format(BigDecimal.ZERO));
    }

    private AppSettings getCurrentSettings() {
        return appSettingsDao.loadSettings()
                .orElse(new AppSettings("en", "IE", "EUR", "exports"));
    }
}