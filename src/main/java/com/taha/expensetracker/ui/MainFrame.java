package com.taha.expensetracker.ui;

import com.taha.expensetracker.dao.AppSettingsDao;
import com.taha.expensetracker.model.AppSettings;
import com.taha.expensetracker.service.LocalizationService;
import com.taha.expensetracker.ui.panel.CategoriesPanel;
import com.taha.expensetracker.ui.panel.ReportsPanel;
import com.taha.expensetracker.ui.panel.SettingsPanel;
import com.taha.expensetracker.ui.panel.TransactionsPanel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.util.Objects;

public class MainFrame extends JFrame {

    private final LocalizationService localizationService;

    public MainFrame() {
        this(loadSettingsFromDatabase());
    }

    public MainFrame(LocalizationService localizationService) {
        this.localizationService = Objects.requireNonNull(localizationService, "LocalizationService must not be null.");
        UITheme.installGlobalLookAndFeel();
        initialiseUi();
    }

    public MainFrame(AppSettings appSettings) {
        this(new LocalizationService(appSettings));
    }

    private void initialiseUi() {
        setTitle(localizationService.get("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        JLabel headerLabel = new JLabel(localizationService.get("app.title"), SwingConstants.CENTER);
        UITheme.styleMainHeader(headerLabel);

        JLabel subtitleLabel = new JLabel("Track. Analyse. Control your money smarter.", SwingConstants.CENTER);
        UITheme.styleSubtitle(subtitleLabel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SURFACE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel headerInner = new JPanel();
        headerInner.setOpaque(false);
        headerInner.setLayout(new javax.swing.BoxLayout(headerInner, javax.swing.BoxLayout.Y_AXIS));
        headerLabel.setAlignmentX(CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        headerInner.add(headerLabel);
        headerInner.add(javax.swing.Box.createVerticalStrut(6));
        headerInner.add(subtitleLabel);

        headerPanel.add(headerInner, BorderLayout.CENTER);

        TransactionsPanel transactionsPanel = new TransactionsPanel(localizationService);
        CategoriesPanel categoriesPanel = new CategoriesPanel(localizationService);
        ReportsPanel reportsPanel = new ReportsPanel(localizationService);
        SettingsPanel settingsPanel = new SettingsPanel(localizationService, this::reloadApplication);

        JTabbedPane tabbedPane = new JTabbedPane();
        UITheme.styleTabbedPane(tabbedPane);

        tabbedPane.addTab(localizationService.get("nav.transactions"), transactionsPanel);
        tabbedPane.addTab(localizationService.get("nav.categories"), categoriesPanel);
        tabbedPane.addTab(localizationService.get("nav.reports"), reportsPanel);
        tabbedPane.addTab(localizationService.get("nav.settings"), settingsPanel);

        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String selectedTitle = tabbedPane.getTitleAt(index);

            if (selectedTitle.equals(localizationService.get("nav.reports"))) {
                reportsPanel.loadReports();
            }
        });

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void reloadApplication() {
        java.awt.EventQueue.invokeLater(() -> {
            dispose();
            new MainFrame().setVisible(true);
        });
    }

    private static AppSettings loadSettingsFromDatabase() {
        return new AppSettingsDao()
                .loadSettings()
                .orElse(new AppSettings("en", "IE", "EUR", "exports"));
    }
}