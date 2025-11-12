package com.beanshogi.gui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import com.beanshogi.util.Position;

public class HighlightLayerPanel extends JPanel {

    private static final int GAP = 3;
    private int cellSize = 100;

    private final Set<Position> highlightedSquares = new HashSet<>();

    public HighlightLayerPanel(int cellSize) {
        setOpaque(false);
        this.cellSize = cellSize;
        setBounds(50, 50, 900, 1200);
    }

    public void highlightSquare(Position position) {
        highlightedSquares.add(position);
        repaint();
    }

    public void highlightSquares(List<Position> positions) {
        highlightedSquares.addAll(positions);
        repaint();
    }

    public void clearSquare(Position position) {
        highlightedSquares.remove(position);
        repaint();
    }

    public void clearAllHighlights() {
        highlightedSquares.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 0, 0, 80)); // translucent red

        for (Position p : highlightedSquares) {
            int px = p.x * (cellSize + GAP);
            int py = p.y * (cellSize + GAP);
            g2.fillRect(px, py, cellSize, cellSize);
        }
    }
}
