package com.beanshogi.gui;

import javax.swing.*;

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
import java.awt.image.BufferStrategy;

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

    public void showGamePlay(Game game) {
        // Remove old game panels to prevent memory leaks and resource conflicts
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof GamePlayFullscreen || comp instanceof GamePlayWindowed) {
                mainPanel.remove(comp);
            }
        }
        if (fullScreen) {
            GamePlayFullscreen fsPanel = new GamePlayFullscreen(this, game);
            mainPanel.add(fsPanel, "GAME_FULLSCREEN");
            currentGameCard = "GAME_FULLSCREEN";
            showCard(currentGameCard);
        } else {
            GamePlayWindowed wPanel = new GamePlayWindowed(this, game);
            mainPanel.add(wPanel, "GAME_WINDOWED");
            currentGameCard = "GAME_WINDOWED";
            showCard(currentGameCard);
        }
    }

    public void openSettings(Runnable backAction) {
        openSettings(backAction, true);
    }

    public void openSettings(Runnable backAction, boolean allowDisplayModeChanges) {
        settingsMenu.setBackAction(backAction);
        settingsMenu.setDisplayModeControlsEnabled(allowDisplayModeChanges);
        showCard("SETTINGS");
    }

    public void openLoadMenu(Runnable backAction) {
        loadMenu.setBackAction(backAction);
        showCard("LOAD");
    }

    public void returnToGame() {
        if (currentGameCard != null) {
            showCard(currentGameCard);
        } else {
            showCard("MAIN");
        }
    }

    public void returnToMainMenu() {
        currentGameCard = null;
        SoundPlayer.stopBackgroundMusic();
        showCard("MAIN");
    }

    public void setSmallWindow() {
        setWindowMode(false);
    }

    public void setFullscreenWindow() {
        setWindowMode(true);
    }

    private void setWindowMode(boolean isFullscreen) {
        dispose();
        setUndecorated(isFullscreen);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (isFullscreen) {
            gd.setFullScreenWindow(this);
            setIgnoreRepaint(true);
            setVisible(true);
            createBufferStrategy(2);
            mainPanel.revalidate();
            // Use buffer strategy for initial paint
            renderWithBufferStrategy();
            // Force repaint to fix disappearing components on some platforms
            mainPanel.repaint();
        } else {
            gd.setFullScreenWindow(null);
            setIgnoreRepaint(false);
            mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setContentPane(mainPanel);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
            // Use normal repaint in windowed mode
            super.repaint();
        }
        fullScreen = isFullscreen;
    }

    /**
     * Render the main panel using BufferStrategy (for fullscreen mode).
     */
    public void renderWithBufferStrategy() {
        if (!fullScreen) return;
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;
        try {
            do {
                do {
                    Graphics g = null;
                    try {
                        g = bs.getDrawGraphics();
                        if (g == null) return; // Defensive: buffer not ready
                        // Clear background
                        g.setColor(Color.BLACK);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        // Paint the main panel
                        mainPanel.paintAll(g);
                    } finally {
                        if (g != null) g.dispose();
                    }
                } while (bs.contentsRestored());
                try {
                    bs.show();
                } catch (NullPointerException e) {
                    // Defensive: buffer may be null on some platforms
                    return;
                }
            } while (bs.contentsLost());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public boolean isFullScreenMode() {
        return fullScreen;
    }

    public void showCard(String cardName) {
        // Clear menu bar when not in game
        if (!cardName.equals("GAME_FULLSCREEN") && !cardName.equals("GAME_WINDOWED")) {
            setJMenuBar(null);
            if (!fullScreen) {
                // Let pack() resize based on content
                pack();
                setLocationRelativeTo(null);
            }
        }
        if ("LEADERBOARD".equals(cardName) && leaderboardMenu != null) {
            leaderboardMenu.refresh();
        }
        cardLayout.show(mainPanel, cardName);
        // Force refresh to fix rendering issues after card switch
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void repaint() {
        if (fullScreen) {
            renderWithBufferStrategy();
        } else {
            super.repaint();
        }
    }
}
