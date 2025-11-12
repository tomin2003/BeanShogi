package com.beanshogi.gui.utils;

import javax.swing.JOptionPane;

public class PromotePopup {
    public static boolean ask() {
        int choice = JOptionPane.showConfirmDialog(
            null,
            "Promote?",
            "Promotion",
            JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }
}

