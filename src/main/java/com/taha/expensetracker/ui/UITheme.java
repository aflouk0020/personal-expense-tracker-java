package com.taha.expensetracker.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public final class UITheme {

    private UITheme() {
    }

    public static final Color BG = new Color(245, 247, 250);
    public static final Color SURFACE = new Color(255, 255, 255);
    public static final Color SURFACE_ALT = new Color(249, 250, 252);
    public static final Color PRIMARY = new Color(36, 99, 235);
    public static final Color PRIMARY_DARK = new Color(30, 64, 175);
    public static final Color ACCENT = new Color(14, 165, 233);
    public static final Color TEXT = new Color(17, 24, 39);
    public static final Color MUTED = new Color(107, 114, 128);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);

    public static final Font FONT_H1 = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_H2 = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_H3 = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);

    public static void installGlobalLookAndFeel() {
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", BG);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Button.font", FONT_BODY);
        UIManager.put("TabbedPane.font", FONT_BODY);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.font", FONT_H3);
    }

    public static Border createPagePadding() {
        return BorderFactory.createEmptyBorder(20, 20, 20, 20);
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static void stylePage(JComponent component) {
        component.setBackground(BG);
        component.setBorder(createPagePadding());
    }

    public static void styleCard(JComponent component) {
        component.setOpaque(true);
        component.setBackground(SURFACE);
        component.setBorder(createCardBorder());
    }

    public static void styleTitle(JLabel label) {
        label.setFont(FONT_H2);
        label.setForeground(TEXT);
    }

    public static void styleMainHeader(JLabel label) {
        label.setFont(FONT_H1);
        label.setForeground(TEXT);
    }

    public static void styleSubtitle(JLabel label) {
        label.setFont(FONT_SMALL);
        label.setForeground(MUTED);
    }

    public static void styleStatus(JLabel label) {
        label.setFont(FONT_SMALL);
        label.setForeground(MUTED);
    }

    public static void styleSuccessStatus(JLabel label, String message) {
        label.setText(message);
        label.setForeground(SUCCESS);
        label.setFont(FONT_SMALL);
    }

    public static void styleErrorStatus(JLabel label, String message) {
        label.setText(message);
        label.setForeground(DANGER);
        label.setFont(FONT_SMALL);
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BODY);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setMargin(new Insets(10, 18, 10, 18));
        button.setOpaque(true);
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(SURFACE);
        button.setForeground(TEXT);
        button.setFont(FONT_BODY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        button.setMargin(new Insets(10, 18, 10, 18));
        button.setOpaque(true);
    }

    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setRowHeight(30);
        table.setGridColor(new Color(238, 241, 245));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_H3);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        DefaultTableCellRenderer headerRenderer =
                (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }

    public static void styleField(JComponent component) {
        component.setFont(FONT_BODY);
        component.setBackground(Color.WHITE);
        component.setForeground(TEXT);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_BODY);
        area.setBackground(Color.WHITE);
        area.setForeground(TEXT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setFont(FONT_BODY);
        tabbedPane.setBackground(BG);
        tabbedPane.setForeground(TEXT);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public static JPanelCard createSummaryCard(String title, JLabel valueLabel) {
        JPanelCard card = new JPanelCard();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SMALL);
        titleLabel.setForeground(MUTED);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(TEXT);

        card.addVertical(titleLabel, valueLabel);
        return card;
    }

    public static final class JPanelCard extends javax.swing.JPanel {
        public JPanelCard() {
            setLayout(new java.awt.BorderLayout());
            styleCard(this);
        }

        public void addVertical(Component... components) {
            javax.swing.JPanel inner = new javax.swing.JPanel();
            inner.setOpaque(false);
            inner.setLayout(new javax.swing.BoxLayout(inner, javax.swing.BoxLayout.Y_AXIS));
            for (Component c : components) {
                c.setForeground(c instanceof JLabel ? ((JLabel) c).getForeground() : TEXT);
                inner.add(c);
                inner.add(javax.swing.Box.createVerticalStrut(6));
            }
            add(inner, java.awt.BorderLayout.CENTER);
        }
    }
}