package com.beanshogi.gui.utils;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class CustomLabel extends JLabel {
    // Constructor loads image from classpath
    public CustomLabel(String resourcePath) {
        setIcon(new ImageIcon(SwingUtils.loadImage(resourcePath)));
        setHorizontalAlignment(CENTER); // center horizontally
        setVerticalAlignment(CENTER);   // center vertically
    }

    public CustomLabel(BufferedImage image) {
        setIcon(new ImageIcon(image));
        setHorizontalAlignment(CENTER); // center horizontally
        setVerticalAlignment(CENTER);   // center vertically
    }
}