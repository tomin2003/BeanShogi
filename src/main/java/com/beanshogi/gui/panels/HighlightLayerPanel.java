package com.beanshogi.gui.panels;

import java.awt.*;
import javax.swing.*;

public class HighlightLayerPanel extends JPanel {

    private static final int BOARD_SIZE = 9;
    private static final int CELL_SIZE = 92;

    public HighlightLayerPanel() {
        setOpaque(false); // transparent background
        setBounds(54, 55, 904, 1225);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 0, 80)); // translucent yellow

        int gap = 3; // space between squares

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                int px = x * (CELL_SIZE + gap);
                int py = y * (CELL_SIZE + gap);
                g2.fillRect(px, py, CELL_SIZE, CELL_SIZE);
            }
        }
    }
}
