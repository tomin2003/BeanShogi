package com.beanshogi.gui;

import javax.swing.*;

import com.beanshogi.gui.panels.*;

import java.awt.*;

public class ShogiWindow extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public ShogiWindow() {
        setTitle("BeanShogi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();

        mainPanel = new JPanel(cardLayout);
        mainPanel.setPreferredSize(new Dimension(1024, 768));

        // Create screens
        MainMenuPanel mainMenu = new MainMenuPanel(this);
        NewGamePanel newGameMenu = new NewGamePanel(this);
        LoadGamePanel loadMenu = new LoadGamePanel(this);
        LeaderboardPanel leaderboardMenu = new LeaderboardPanel(this);
        SettingsPanel settingsMenu = new SettingsPanel(this);

        // Register cards
        mainPanel.add(mainMenu, "MAIN");
        mainPanel.add(newGameMenu, "NEW");
        mainPanel.add(loadMenu, "LOAD");
        mainPanel.add(leaderboardMenu, "LEADERBOARD");
        mainPanel.add(settingsMenu, "SETTINGS");

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }
}
