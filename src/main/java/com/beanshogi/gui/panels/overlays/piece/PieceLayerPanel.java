package com.beanshogi.gui.panels.overlays.piece;

import javax.swing.JPanel;

import com.beanshogi.util.Position;

public class PieceLayerPanel extends JPanel {

    private int cellSize;
    private Position gap;

    public PieceLayerPanel(int cellSize, Position gap) {
        setLayout(null); // free positioning
        setOpaque(false); // board visible behind
        setFocusable(true); // Allow mouse events
        this.cellSize = cellSize;
        this.gap = gap;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Position getGap() {
        return gap;
    }

    public void addPiece(PieceComponent piece, Position pos) {
        int x = pos.x * (cellSize + gap.x);
        int y = pos.y * (cellSize + gap.y);

        piece.setLocation(x, y);
        add(piece);
        repaint();
    }
}