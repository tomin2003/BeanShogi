package com.beanshogi.gui.panels.menucards;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.*;

public class MainMenuPanel extends BackgroundPanel {
    
    public MainMenuPanel(ShogiWindow window) {
        super("/shogi_bg.png");
        
        add(Box.createVerticalStrut(40));
        CustomLabel logoLabel = new CustomLabel("/logo.png");
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(logoLabel);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create new buttons into a list
        List<JButton> menuButtons = new ArrayList<>();

        menuButtons.add(SwingUtils.makeButton("New Game", e -> window.showCard("NEW")));
        menuButtons.add(SwingUtils.makeButton("Load Game", e -> window.openLoadMenu(() -> window.showCard("MAIN"))));
        menuButtons.add(SwingUtils.makeButton("Leaderboard", e -> window.showCard("LEADERBOARD")));
        menuButtons.add(SwingUtils.makeButton("Settings", e -> window.openSettings(() -> window.showCard("MAIN"))));
        menuButtons.add(SwingUtils.makeButton("Exit", e -> {
            SoundPlayer.stopBackgroundMusic();
            System.exit(0);
        }));

        // Top glue: pushes buttons down proportionally
        add(Box.createVerticalGlue());

        for (JButton button : menuButtons) {
            button.setAlignmentX(CENTER_ALIGNMENT);
            add(button);
            add(Box.createVerticalStrut(35));
        }

        // Bottom glue: pushes buttons up proportionally
        add(Box.createVerticalGlue());

    }
}
