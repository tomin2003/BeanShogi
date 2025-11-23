package com.beanshogi;
import com.beanshogi.gui.ShogiWindow;

/**
 * Entry point for BeanShogi application.
 * Launches the GUI on the Event Dispatch Thread for thread safety.
 */
public class Main {
    public static void main(String[] args) {
        // Launch GUI on Event Dispatch Thread (EDT) to avoid threading issues
        javax.swing.SwingUtilities.invokeLater(ShogiWindow::new);
    }
}