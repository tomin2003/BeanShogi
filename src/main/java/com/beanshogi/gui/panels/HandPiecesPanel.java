package com.beanshogi.gui.panels;

import javax.swing.JPanel;

import com.beanshogi.gui.piece.PieceComponent;

public class HandPiecesPanel extends JPanel {

    private static final int GAP = 2;

    public HandPiecesPanel() {
        setLayout(null); // free positioning
        setOpaque(false); // hand table visible behind
    }

    public void addPiece(PieceComponent piece, int row, int col, int cellSize) {
        int x = col * (cellSize + GAP);
        int y = row * (cellSize + GAP);

        piece.setLocation(x, y);
        add(piece);
        repaint();
    }
}