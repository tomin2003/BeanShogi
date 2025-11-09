package com.beanshogi.gui.panels.menu;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.*;

public class LoadGamePanel extends JPanel {

    public LoadGamePanel(ShogiWindow window) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        // 400 px offset downwards for the buttons
        add(Box.createVerticalStrut(700));
        add(backButton);
        add(Box.createVerticalStrut(25));
    }
}
