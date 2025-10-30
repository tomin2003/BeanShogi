package com.beanshogi;

import com.beanshogi.gui.ShogiWindow;

public class Main {
    public static void main(String[] args) {
        // Separate GUI thread
        javax.swing.SwingUtilities.invokeLater(ShogiWindow::new);
    }
}