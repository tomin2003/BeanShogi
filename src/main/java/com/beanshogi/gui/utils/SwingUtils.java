package com.beanshogi.gui.utils;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class SwingUtils {

    public static JButton makeButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }

    public static BufferedImage loadImage(String resourcePath) {
        try {
            // Load from resources
            return ImageIO.read(SwingUtils.class.getResourceAsStream(resourcePath));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Failed to load image: " + resourcePath);
            return null;
        }
    }
}
