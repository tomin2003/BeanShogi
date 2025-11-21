package com.beanshogi.gui.panels.menucards;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.*;

public class LeaderboardPanel extends JPanel {

    public LeaderboardPanel(ShogiWindow window) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        // 400 px offset downwards for the buttons
        add(Box.createVerticalStrut(700));
        add(backButton);
        add(Box.createVerticalStrut(25));
    }
}
