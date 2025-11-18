package com.beanshogi.gui.util;

import javax.swing.JOptionPane;

public class Popups {
    public static boolean askPromotion() {
        int choice = JOptionPane.showConfirmDialog(
            null,
            "Promote?",
            "Promotion",
            JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }

    public static void showGameOver(String winnerName) {
        JOptionPane.showMessageDialog(
            null,
            winnerName + " wins!\n\nCheckmate!",
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}

