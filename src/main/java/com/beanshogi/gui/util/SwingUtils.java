package com.beanshogi.gui.util;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JMenuItem;

public class SwingUtils {

    /**
     * Helper function to make JButtons inline, with text and action listeners.
     * @param text Button label text
     * @param action Action listener
     * @return constructed JButton
     */
    public static JButton makeButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }

    /**
     * Helper function to make JMenuItems inline, with text and action listeners.
     * @param text Menuitem label text
     * @param action Action listener
     * @return constructed JMenuItem
     */
    public static JMenuItem makeMenuItem(String text, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(action);
        return menuItem;
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
