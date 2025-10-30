package com.beanshogi.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JPanelWithBackground extends JPanel {

    private BufferedImage backgroundImage;

    // Constructor loads image from classpath
    public JPanelWithBackground(String resourcePath) {
        try {
            // Use getResourceAsStream to load from src/main/resources
            backgroundImage = ImageIO.read(getClass().getResourceAsStream(resourcePath));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Failed to load background: " + resourcePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // Draw image scaled to panel size
            Graphics2D g2d = (Graphics2D) g.create();

            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);    
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}
