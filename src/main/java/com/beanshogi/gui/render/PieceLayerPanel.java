package com.beanshogi.gui.render;

import javax.swing.JPanel;

public class PieceLayerPanel extends JPanel {

    private static final int GAP = 3;

    public PieceLayerPanel() {
        setLayout(null); // free positioning
        setOpaque(false); // board visible behind
    }

    public void addPiece(PieceComponent piece, int row, int col, int cellSize) {
        int x = col * (cellSize + GAP);
        int y = row * (cellSize + GAP);

        piece.setLocation(x, y);
        add(piece);
        repaint();
    }
}