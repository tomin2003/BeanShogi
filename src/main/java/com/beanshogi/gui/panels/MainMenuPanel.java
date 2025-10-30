package com.beanshogi.gui.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.*;

public class MainMenuPanel extends JPanelWithBackground {

     public MainMenuPanel(ShogiWindow window) {
        super("/sprites/bg_w_logo.png");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create new buttons into a list
        List<JButton> menuButtons = new ArrayList<>();

        menuButtons.add(SwingUtils.makeButton("Új játék", e -> window.showCard("NEW")));
        menuButtons.add(SwingUtils.makeButton("Játék betöltése", e -> window.showCard("LOAD")));
        menuButtons.add(SwingUtils.makeButton("Ranglista", e -> window.showCard("LEADERBOARD")));
        menuButtons.add(SwingUtils.makeButton("Beállítások", e -> window.showCard("SETTINGS")));
        menuButtons.add(SwingUtils.makeButton("Kilépés", e -> System.exit(0)));

        // 400 px offset downwards for the buttons
        add(Box.createVerticalStrut(400));

        // Add 25 px offset between each button
        for (JButton button : menuButtons) {
            add(button);
            add(Box.createVerticalStrut(25));
        }
    }
}
