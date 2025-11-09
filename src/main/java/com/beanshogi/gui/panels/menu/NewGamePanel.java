package com.beanshogi.gui.panels.menu;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.*;

public class NewGamePanel extends JPanel {

    public NewGamePanel(ShogiWindow window) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton playButton = SwingUtils.makeButton("Play", e -> window.showGamePlay());

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        // 400 px offset downwards for the buttons
        add(Box.createVerticalStrut(700));
        add(playButton);
        add(Box.createVerticalStrut(25));
        add(backButton);
        add(Box.createVerticalStrut(25));
    }
}
