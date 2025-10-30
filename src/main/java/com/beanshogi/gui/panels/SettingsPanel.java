package com.beanshogi.gui.panels;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.*;

public class SettingsPanel extends JPanelWithBackground {

    public SettingsPanel(ShogiWindow window) {
        super("/sprites/bg.png");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton backButton = SwingUtils.makeButton("Vissza", e -> window.showCard("MAIN"));

        // 400 px offset downwards for the buttons
        add(Box.createVerticalStrut(700));
        add(backButton);
        add(Box.createVerticalStrut(25));
    }
}
