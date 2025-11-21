package com.beanshogi.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.beanshogi.game.Game;
import com.beanshogi.gui.ShogiWindow;

public class SwingUtils {

    /**
     * Helper function to make consistently sized JButtons inline, with text and action listener.
     * @param text Button label text
     * @param action Action listener
     * @return constructed JButton
     */
    public static JButton makeButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }

    /**
     * Helper function to make JMenuItems inline, with text and action listener.
     * @param text Menu item label text
     * @param action Action listener
     * @return the constructed JMenuItem
     */
    public static JMenuItem makeMenuItem(String text, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(action);
        return menuItem;
    }

    /**
     * Helper function to create a reusable menubar for both gameplay panels.
     * @param window The ShogiWindow JFrame which allows for swithing cardLayouts
     * @param panelWidth The width component of panel the menubar is used in
     * @param game Reference to the actual game
     * @return the created menubar
     */
    public static JMenuBar makeMenuBar(ShogiWindow window, int panelWidth, Game game) {
        // Create overlay menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(new Color(255,255,255,120)); // translucent white menubar

        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(SwingUtils.makeMenuItem("New Game", e -> window.showGamePlay(new Game(game.getBoard().getPlayers()))));
        gameMenu.add(SwingUtils.makeMenuItem("Main Menu", e -> window.showCard("MAIN")));
        gameMenu.addSeparator();
        gameMenu.add(SwingUtils.makeMenuItem("Settings", e -> window.showCard("SETTINGS")));
        gameMenu.addSeparator();
        gameMenu.add(SwingUtils.makeMenuItem("Exit", e -> System.exit(0)));

        menuBar.add(gameMenu);
        int menuHeight = menuBar.getPreferredSize().height;
        menuBar.setBounds(0, 0, 1280, menuHeight);
        return menuBar;
    }

    /**
     * Load BufferedImages from classpath with error handling.
     * @param resourcePath Path of image resource
     * @return the loaded image
     */
    public static BufferedImage loadImage(String resourcePath) {
        try {
            // Load from resources
            return ImageIO.read(SwingUtils.class.getResourceAsStream(resourcePath));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("Failed to load image: " + resourcePath);
            return null;
        }
    }
}
