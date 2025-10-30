package com.beanshogi.gui.utils;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class SwingUtils {

    public static JButton makeButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }
}
