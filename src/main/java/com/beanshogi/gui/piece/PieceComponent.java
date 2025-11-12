package com.beanshogi.gui.piece;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.beanshogi.util.Sides;

public class PieceComponent extends JPanel {

    private final BufferedImage image;
    private final Sides pieceSide;

    public PieceComponent(BufferedImage img, Sides pieceSide, int size) {
        this.image = img;
        this.pieceSide = pieceSide;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setSize(size, size); // because we'll position with setLocation
        // Later: addMouseListener for selecting/dragging
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int imgW = image.getWidth();
        int imgH = image.getHeight();
        int boxW = getWidth();
        int boxH = getHeight();

        // Centering offsets (no scaling)
        int x = (boxW - imgW) / 2;
        int y = (boxH - imgH) / 2;

        // Flip gote pieces, render sente pieces normally
        if (pieceSide == Sides.GOTE) {
            AffineTransform at = new AffineTransform();
            at.translate(x + imgW / 2.0, y + imgH / 2.0);
            at.rotate(Math.toRadians(180));
            at.translate(-imgW / 2.0, -imgH / 2.0);
            g2d.drawImage(image, at, null);
        } else {
            g2d.drawImage(image, x, y, null);
        }
        g2d.dispose();
    }

}