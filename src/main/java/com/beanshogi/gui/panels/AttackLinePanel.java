package com.beanshogi.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.beanshogi.gui.listeners.KingCheckListener;
import com.beanshogi.model.Piece;

// TODO: Implement correctly!
public class AttackLinePanel extends JPanel implements KingCheckListener {
    private Piece attackingPiece = null;
    private Piece attackedKing = null;
    private int cellSize;
    private static final int GAP = 3;

    public AttackLinePanel(int cellSize) {
        this.cellSize = cellSize;
        setOpaque(false); // transparent overlay
    }

    /**
     * Incremental update for single attacker
     */
    @Override
    public void onKingInCheck(boolean inCheck, Piece attackedKing, Piece attackingPiece) {
        if (!inCheck) {
            this.attackedKing = null;
            this.attackingPiece = null;
        } else {
            this.attackedKing = attackedKing;
            this.attackingPiece = attackingPiece;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (attackedKing == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int kingX = attackedKing.getPosition().x * cellSize + cellSize / 2;
        int kingY = attackedKing.getPosition().y * cellSize + cellSize / 2;
        System.out.println(kingX + ", " + kingY);

        int atX = attackingPiece.getPosition().x * cellSize + cellSize / 2;
        int atY = attackingPiece.getPosition().y * cellSize + cellSize / 2;
        g2.drawLine(atX, atY, kingX, kingY);
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        repaint();
    }
}
