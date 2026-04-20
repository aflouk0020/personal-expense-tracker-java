package com.taha.expensetracker;

import com.formdev.flatlaf.FlatLightLaf;
import com.taha.expensetracker.ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainFrame().setVisible(true);
        });
    }
}