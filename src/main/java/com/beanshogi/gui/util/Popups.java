package com.beanshogi.gui.util;

import java.awt.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.JOptionPane;

import com.beanshogi.core.game.Sides;


/**
 * Utility class for displaying dialog popups to the user.
 * Handles promotion decisions, game over notifications, and confirmation dialogs.
 */
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

    public static void showGameOver(Component parent, String winnerName, String reason) {
        String message;
        if ("Draw".equals(winnerName)) {
            message = "Game ended in a draw!\n\n" + reason;
        } else {
            message = winnerName + " wins!\n\n" + reason;
        }
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showGameSaved(Component parent, String filePath) {
        JOptionPane.showMessageDialog(
            parent,
            "Game state saved to: " + filePath,
            "Save game",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static Sides askResignationSide(Component parent, LinkedHashMap<Sides, String> sideNames) {
        if (sideNames == null || sideNames.isEmpty()) {
            return null;
        }

        if (sideNames.size() == 1) {
            Sides onlySide = sideNames.keySet().iterator().next();
            String onlyName = sideNames.get(onlySide);
            return confirmResignation(parent, onlyName) ? onlySide : null;
        }

        Sides[] sides = sideNames.keySet().toArray(new Sides[0]);
        Object[] options = Arrays.stream(sides)
            .map(side -> String.format("%s (%s)", side, sideNames.get(side)))
            .toArray();

        int choice = JOptionPane.showOptionDialog(
            parent,
            "Which side is resigning?",
            "Resign",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == JOptionPane.CLOSED_OPTION || choice < 0 || choice >= sides.length) {
            return null;
        }

        Sides selected = sides[choice];
        String selectedName = sideNames.get(selected);
        return confirmResignation(parent, selectedName) ? selected : null;
    }

    private static boolean confirmResignation(Component parent, String playerName) {
        int confirm = JOptionPane.showConfirmDialog(
            parent,
            playerName + " will resign. Are you sure?",
            "Confirm Resign",
            JOptionPane.YES_NO_OPTION
        );
        return confirm == JOptionPane.YES_OPTION;
    }

    public static void showNoHumanResign(Component parent) {
        JOptionPane.showMessageDialog(
            parent,
            "Only human players can resign in this match.",
            "Resign",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static boolean confirmReturnToMainMenu(Component parent) {
        int choice = JOptionPane.showConfirmDialog(
            parent,
            "Return to main menu? Current game progress will be lost.",
            "Return to Main Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }

    public static boolean confirmExitGame(Component parent) {
        int choice = JOptionPane.showConfirmDialog(
            parent,
            "Exit game? Current game progress will be lost.",
            "Exit Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
}

