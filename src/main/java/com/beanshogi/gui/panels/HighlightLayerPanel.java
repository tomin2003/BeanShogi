package com.beanshogi.gui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import com.beanshogi.util.Position;

public class HighlightLayerPanel extends JPanel {

    private static final int CELL_SIZE = 90;
    private static final int GAP = 3; // space between squares

    private final Set<Position> highlightedSquares = new HashSet<>();

    public HighlightLayerPanel() {
        setOpaque(false); // transparent background
        setBounds(50, 50, 900, 1200);
    }

    // Add a square to highlight
    public void highlightSquare(Position position) {
        highlightedSquares.add(position);
        repaint();
    }

    // Remove a square from highlight
    public void clearSquare(Position position) {
        highlightedSquares.remove(position);
        repaint();
    }

    // Clear all highlights
    public void clearAllHighlights() {
        highlightedSquares.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 0, 80)); // translucent yellow

        for (Position p : highlightedSquares) {
            int px = p.x * (CELL_SIZE + GAP);
            int py = p.y * (CELL_SIZE + GAP);
            g2.fillRect(px, py, CELL_SIZE, CELL_SIZE);
        }
    }
}