package com.beanshogi.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * JPanel that draws a filled rectangle with specified color and rounded corners.
 */
public class FilledRectanglePanel extends JPanel {

    private Color color;
    private int roundingRadius = 0;

    public FilledRectanglePanel(int width, int height, int roundingRadius, Color color) {
        this.setPreferredSize(new Dimension(width, height));
        this.roundingRadius = roundingRadius;
        this.color = color;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), roundingRadius, roundingRadius);

    }
}
