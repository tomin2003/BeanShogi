package com.beanshogi.gui.panels;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.BackgroundPanel;
import com.beanshogi.gui.utils.SwingUtils;

public class GamePlayFullscreen extends BackgroundPanel {
    public GamePlayFullscreen(ShogiWindow window) {
        super("/sprites/gamebg16_9.png");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Back button at bottom
        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        add(Box.createVerticalStrut(900)); // taller offset for fullscreen
        add(backButton);
        add(Box.createVerticalStrut(25));
    }
}
