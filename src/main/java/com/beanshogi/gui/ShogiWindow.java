package com.beanshogi.gui;

import javax.swing.*;

import com.beanshogi.core.game.Controller;
import com.beanshogi.core.game.Game;
import com.beanshogi.gui.listeners.event.WindowCloseListener;
import com.beanshogi.gui.panels.gameplay.GamePlayFullscreen;
import com.beanshogi.gui.panels.gameplay.GamePlayWindowed;
import com.beanshogi.gui.panels.menucards.LeaderboardPanel;
import com.beanshogi.gui.panels.menucards.LoadGamePanel;
import com.beanshogi.gui.panels.menucards.MainMenuPanel;
import com.beanshogi.gui.panels.menucards.NewGamePanel;
import com.beanshogi.gui.panels.menucards.SettingsPanel;
import com.beanshogi.gui.util.SoundPlayer;
import com.beanshogi.gui.util.SwingUtils;

import java.awt.*;

/**
 * The main JFrame for BeanShogi.
 */
public class ShogiWindow extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean fullScreen = false;
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 960;
    private SettingsPanel settingsMenu;
    private LoadGamePanel loadMenu;
    private LeaderboardPanel leaderboardMenu;
    private String currentGameCard; // Tracks which game view is active (fullscreen/windowed)
    private Controller currentController = null; // Track the current Controller for shutdown

    public ShogiWindow() {
        setTitle("BeanShogi");
        setIconImage(SwingUtils.loadImage("/icon.png"));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Add window listener to stop music on exit
        addWindowListener(new WindowCloseListener());
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create screens
        MainMenuPanel mainMenu = new MainMenuPanel(this);
        NewGamePanel newGameMenu = new NewGamePanel(this);
        loadMenu = new LoadGamePanel(this);
        leaderboardMenu = new LeaderboardPanel(this);
        settingsMenu = new SettingsPanel(this);

        // Register cards
        mainPanel.add(mainMenu, "MAIN");
        mainPanel.add(newGameMenu, "NEW");
        mainPanel.add(loadMenu, "LOAD");
        mainPanel.add(leaderboardMenu, "LEADERBOARD");
        mainPanel.add(settingsMenu, "SETTINGS");

        setContentPane(mainPanel);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Show either full screen or windowed gameplay panels.
     * @param game the current game
     */
    public void showGamePlay(Game game) {
        // Shutdown previous controller if present
        if (currentController != null) {
            currentController.shutdown();
            currentController = null;
        }
        // Remove old game panels to prevent memory leaks and resource conflicts
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof GamePlayFullscreen || comp instanceof GamePlayWindowed) {
                mainPanel.remove(comp);
            }
        }
        if (fullScreen) {
            GamePlayFullscreen fsPanel = new GamePlayFullscreen(this, game);
            // Store controller reference
            currentController = fsPanel.getController();
            mainPanel.add(fsPanel, "GAME_FULLSCREEN");
            currentGameCard = "GAME_FULLSCREEN";
            showCard(currentGameCard);
        } else {
            GamePlayWindowed wPanel = new GamePlayWindowed(this, game);
            // Store controller reference
            currentController = wPanel.getController();
            mainPanel.add(wPanel, "GAME_WINDOWED");
            currentGameCard = "GAME_WINDOWED";
            showCard(currentGameCard);
        }
    }

    /**
     * Lambda friendly version of openSettings(), allows display mode changes.
     * @param backAction the action to be performed for the back button press
     */
    public void openSettings(Runnable backAction) {
        openSettings(backAction, true);
    }

    /**
     * Navigates to the settings menucard.
     * @param backAction the action to be performed for the back button press
     * @param allowDisplayModeChanges allow the display mode change radio buttons or not
     */
    public void openSettings(Runnable backAction, boolean allowDisplayModeChanges) {
        settingsMenu.setBackAction(backAction);
        settingsMenu.setDisplayModeControlsEnabled(allowDisplayModeChanges);
        showCard("SETTINGS");
    }

    /**
     * Navigates to the load menucard.
     * @param backAction The action to be performed for the back button press
     */
    public void openLoadMenu(Runnable backAction) {
        loadMenu.setBackAction(backAction);
        showCard("LOAD");
    }

    /**
     * The default action for returning from menus.
     */
    public void returnToGame() {
        if (currentGameCard != null) {
            showCard(currentGameCard);
        } else {
            showCard("MAIN");
        }
    }

    /**
     * Returning to main menu from game, shuts down running game processes.
     */
    public void returnToMainMenu() {
        // Shutdown controller if returning to main menu from a game
        if (currentController != null) {
            currentController.shutdown();
            currentController = null;
        }
        currentGameCard = null;
        SoundPlayer.stopBackgroundMusic();
        showCard("MAIN");
    }

    /**
     * Sets fullscreen mode explicitly to false
     */
    public void setSmallWindow() {
        setWindowMode(false);
    }

    /**
     * Sets fullscreen mode explicitly to true
     */
    public void setFullscreenWindow() {
        setWindowMode(true);
    }

    /**
     * Change the display mode of the frame.
     * @param isFullscreen true - fullscreen, false - windowed
     */
    private void setWindowMode(boolean isFullscreen) {
        dispose();
        setUndecorated(isFullscreen);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (isFullscreen) { // Perform entering full screen mode
            gd.setFullScreenWindow(this);
            setVisible(true);
            mainPanel.revalidate();
            repaint();
        } else { // Perform going back to windowed mode from full screen
            gd.setFullScreenWindow(null);
            mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setContentPane(mainPanel);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
            repaint();
        }
        fullScreen = isFullscreen;
    }

    /**
     * Public getter of full screen mode state.
     * @return fullscreen mode state
     */
    public boolean isFullScreenMode() {
        return fullScreen;
    }

    /**
     * Show menucard
     * @param cardName name of card to be shown
     */
    public void showCard(String cardName) {
        // Clear menu bar when not in game
        if (!cardName.equals("GAME_FULLSCREEN") && !cardName.equals("GAME_WINDOWED")) {
            setJMenuBar(null);
            if (!fullScreen) {
                // Let pack() resize based on content
                pack();
            }
        }
        if (cardName.equals("LEADERBOARD") && leaderboardMenu != null) {
            leaderboardMenu.refresh();
        }
        cardLayout.show(mainPanel, cardName);
    }
}
