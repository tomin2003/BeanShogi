package com.beanshogi.gui.util;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Custom JLabel that centers an image icon loaded from a resource path or BufferedImage.
 */
public class CustomLabel extends JLabel {
    public CustomLabel(String resourcePath) {
        setIcon(new ImageIcon(SwingUtils.loadImage(resourcePath)));
        setHorizontalAlignment(CENTER); 
        setVerticalAlignment(CENTER);  
    }

    public CustomLabel(BufferedImage image) {
        setIcon(new ImageIcon(image));
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }
}