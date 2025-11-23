package com.beanshogi.gui.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import com.beanshogi.core.util.Position;

public class HighlightLayerPanel extends JPanel {

    private int cellSize;
    private Position gap;
    private Color color;

    private final Set<Position> highlightedSquares = new HashSet<>();

    public HighlightLayerPanel(int cellSize, Position gap, Color color) {
        setOpaque(false);
        this.cellSize = cellSize;
        this.gap = gap;
        this.color = color;
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
        g2.setColor(color);

        for (Position p : highlightedSquares) {
            int px = p.x * (cellSize + gap.x);
            int py = p.y * (cellSize + gap.y);
            g2.fillRect(px, py, cellSize, cellSize);
        }
    }
}
