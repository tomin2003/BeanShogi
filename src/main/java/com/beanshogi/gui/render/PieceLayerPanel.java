package com.beanshogi.gui.render;

import javax.swing.JPanel;

public class PieceLayerPanel extends JPanel {

    public PieceLayerPanel() {
        setLayout(null); // free positioning
        setOpaque(false); // board visible behind
    }

    public void addPiece(PieceComponent piece, int row, int col, int cellSize) {
        int x = col * cellSize;
        int y = row * cellSize;
        piece.setLocation(x, y);
        add(piece);
        repaint();
    }
}