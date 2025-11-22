package com.beanshogi.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.beanshogi.game.Game;
import com.beanshogi.game.Player;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.io.GameSaveLoad;
import com.beanshogi.util.Sides;

public class SwingUtils {

    private static final Color MENU_BAR_BG = new Color(255,255,255,120);

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
    private static JMenuItem menuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        return item;
    }

    /**
     * Helper function to create a reusable menubar for both gameplay panels.
     * @param window The ShogiWindow JFrame which allows for switching cardLayouts
     * @param game Reference to the actual game
     * @return the created menubar
     */
    public static JMenuBar makeMenuBar(ShogiWindow window, Game game, Consumer<Sides> resignAction) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(MENU_BAR_BG);

        menuBar.add(buildFileMenu(window, game));
        menuBar.add(buildGameMenu(window, game, resignAction));

        int menuHeight = menuBar.getPreferredSize().height;
        menuBar.setBounds(0, 0, 1280, menuHeight);
        return menuBar;
    }

    /**
     * Builds the file menu for the menubar.
     * @param window the main frame
     * @param game the game on which file operations are done
     * @return built file menu
     */
    private static JMenu buildFileMenu(ShogiWindow window, Game game) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(menuItem("New Game", e -> window.showGamePlay(new Game(game.getBoard().getPlayers()))));
        fileMenu.add(menuItem("Save game", e -> Popups.showGameSaved(window, GameSaveLoad.save(game))));
        fileMenu.add(menuItem("Load game", e -> window.openLoadMenu(window::returnToGame)));
        fileMenu.addSeparator();
        fileMenu.add(menuItem("Exit", e -> System.exit(0)));
        return fileMenu;
    }

    /**
     * Builds the game menu for the menubar.
     * @param window the main frame
     * @param game the game on which resignation is done
     * @param resignAction consumer for which action needs to be done
     * @return built game menu
     */
    private static JMenu buildGameMenu(ShogiWindow window, Game game, Consumer<Sides> resignAction) {
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(menuItem("Main Menu", e -> {
            if (Popups.confirmReturnToMainMenu(window)) {
                window.returnToMainMenu();
            }
        }));
        gameMenu.add(menuItem("Settings", e -> window.openSettings(window::returnToGame, false)));
        gameMenu.addSeparator();
        gameMenu.add(menuItem("Resign", e -> handleResign(window, game, resignAction)));
        return gameMenu;
    }

    /**
     * Handles the action of resignation, by prompting the user.
     * @param window the main frame
     * @param game the game on which resignation is done
     * @param resignAction consumer for which action needs to be done
     */
    private static void handleResign(ShogiWindow window, Game game, Consumer<Sides> resignAction) {
        if (resignAction == null) {
            return;
        }
        try {
            LinkedHashMap<Sides, String> resignableSides = new LinkedHashMap<>();
            Player sente = game.getBoard().getPlayer(Sides.SENTE);
            Player gote = game.getBoard().getPlayer(Sides.GOTE);
            if (sente != null && sente.isHuman()) {
                resignableSides.put(Sides.SENTE, sente.getName());
            }
            if (gote != null && gote.isHuman()) {
                resignableSides.put(Sides.GOTE, gote.getName());
            }
            if (resignableSides.isEmpty()) {
                Popups.showNoHumanResign(window);
                return;
            }
            Sides resigningSide = Popups.askResignationSide(window, resignableSides);
            if (resigningSide != null) {
                resignAction.accept(resigningSide);
            }
        } catch (Exception ex) {
            window.openSettings(window::returnToGame, false);
        }
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
