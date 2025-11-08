package com.beanshogi.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundPanel extends JPanel {

    private BufferedImage backgroundImage;

    // Constructor loads image from classpath
    public BackgroundPanel(String resourcePath) {
        backgroundImage = SwingUtils.loadImage(resourcePath);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // Draw image scaled to panel size
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setDoubleBuffered(true);

            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Fill the rest of the space with black
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, panelWidth, panelHeight);

            // Compute scale factor to cover the panel
            double scale = Math.max((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
            int newWidth = (int) (imgWidth * scale);
            int newHeight = (int) (imgHeight * scale);

            // Center the image
            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;
            System.out.println(panelWidth + " " + panelHeight);
            g2d.drawImage(backgroundImage, x, y, newWidth, newHeight, this);

            g2d.dispose();
        }
    }
}
