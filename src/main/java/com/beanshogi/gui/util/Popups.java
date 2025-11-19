package com.beanshogi.gui.util;

import javax.swing.JOptionPane;
import java.awt.Component;

public class Popups {
    public static boolean askPromotion(Component parent) {
        int choice = JOptionPane.showConfirmDialog(
            parent,
            "Promote?",
            "Promotion",
            JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }

    public static void showGameOver(Component parent, String winnerName) {
        JOptionPane.showMessageDialog(
            parent,
            winnerName + " wins!\n\nCheckmate!",
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}

