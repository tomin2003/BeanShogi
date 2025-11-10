package com.beanshogi.gui;

import javax.swing.*;

import com.beanshogi.gui.panels.*;
import com.beanshogi.gui.panels.menu.LeaderboardPanel;
import com.beanshogi.gui.panels.menu.LoadGamePanel;
import com.beanshogi.gui.panels.menu.MainMenuPanel;
import com.beanshogi.gui.panels.menu.NewGamePanel;
import com.beanshogi.gui.panels.menu.SettingsPanel;
import com.beanshogi.gui.utils.SwingUtils;

import java.awt.*;

public class ShogiWindow extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean fullScreen = false;

    public ShogiWindow() {
        setTitle("BeanShogi");
        setIconImage(SwingUtils.loadImage("/sprites/icon.png"));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();

        mainPanel = new JPanel(cardLayout);

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

    public void showGamePlay() {
        if (fullScreen) {
            GamePlayFullscreen fsPanel = new GamePlayFullscreen(this);
            mainPanel.add(fsPanel, "GAME_FULLSCREEN");
            showCard("GAME_FULLSCREEN");
        } else {
            GamePlayWindowed wPanel = new GamePlayWindowed(this);
            mainPanel.add(wPanel, "GAME_WINDOWED");
            showCard("GAME_WINDOWED");
        }
    }

    public void setSmallWindow() {
        // Dispose current frame and restore decoration
        dispose();
        setUndecorated(false);

        // Get the graphics device and release from full-screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        gd.setFullScreenWindow(null);

        // Set preferred frame size, and then wrap around content
        setPreferredSize(new Dimension(1280,960));
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // Make frame visible again
        setVisible(true);
        fullScreen = false;
    }

    public void setFullscreenWindow() {
        // Dispose current frame and remove decoration
        dispose();
        setUndecorated(true);
        
        // Get the device and enter into full screen mode
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
   
        gd.setFullScreenWindow(this);

        // Make frame visible again
        setVisible(true);
        fullScreen = true;
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }
}
