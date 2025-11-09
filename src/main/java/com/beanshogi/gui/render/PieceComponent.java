package com.beanshogi.gui.render;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PieceComponent extends JPanel {

    private final BufferedImage image;

    public PieceComponent(BufferedImage img, int size) {
        this.image = img;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setSize(size, size); // because we'll position with setLocation
        // Later: addMouseListener for selecting/dragging
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g2d.dispose();
    }
}