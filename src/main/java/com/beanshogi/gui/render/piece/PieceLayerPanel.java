package com.beanshogi.gui.render.piece;

import javax.swing.JPanel;

import com.beanshogi.core.util.Position;

/**
 * Panel layer for rendering Shogi pieces on the board.
 */
public class PieceLayerPanel extends JPanel {

    private int cellSize;
    private Position gap;

    public PieceLayerPanel(int cellSize, Position gap) {
        setLayout(null);
        setOpaque(false);
        setFocusable(true);
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