package com.beanshogi.gui.panels.overlays;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.beanshogi.gui.listeners.KingCheckListener;
import com.beanshogi.model.CheckEvent;
import com.beanshogi.model.Piece;

public class AttackLinePanel extends JPanel implements KingCheckListener {
    private final List<CheckEvent> checkEvents = new ArrayList<>();
    private int cellSize;
    private int gap;

    public AttackLinePanel(int cellSize, int gap) {
        this.cellSize = cellSize;
        this.gap = gap;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (checkEvents.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (CheckEvent ce : checkEvents) {
            Piece king = ce.getKing();
            Piece attacker = ce.getAttacker();
            if (king == null || attacker == null) continue;

            int kingX = king.getBoardPosition().x * (gap + cellSize) + cellSize / 2;
            int kingY = king.getBoardPosition().y * (gap + cellSize) + cellSize / 2;
            int atX = attacker.getBoardPosition().x * (gap + cellSize) + cellSize / 2;
            int atY = attacker.getBoardPosition().y * (gap + cellSize) + cellSize / 2;

            g2.drawLine(atX, atY, kingX, kingY);
        }
    }

    @Override
    public void onKingInCheck(List<CheckEvent> checkEvents) {
        this.checkEvents.clear();
        this.checkEvents.addAll(checkEvents);
        repaint();
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
        repaint();
    }
}
