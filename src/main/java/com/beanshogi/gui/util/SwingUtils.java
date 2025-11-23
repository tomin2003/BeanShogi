package com.beanshogi.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import com.beanshogi.core.game.Game;
import com.beanshogi.core.game.Player;
import com.beanshogi.core.game.Sides;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.io.GameSaveLoad;

/**
 * Helper class, defines useful helpers for Swing.
 */
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
     * Creates a JLabel with the specified text.
     * @param text The label text
     * @return the constructed JLabel
     */
    public static JLabel makeLabel(String text) {
        return new JLabel(text);
    }

    /**
     * Creates a center-aligned JLabel with the specified text.
     * @param text The label text
     * @return the constructed JLabel
     */
    public static JLabel makeCenteredLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Creates a JTextField with initial text and column width.
     * @param text Initial text
     * @param columns Number of columns
     * @return the constructed JTextField
     */
    public static JTextField makeTextField(String text, int columns) {
        return new JTextField(text, columns);
    }

    /**
     * Creates a JRadioButton with the specified text.
     * @param text The button text
     * @return the constructed JRadioButton
     */
    public static JRadioButton makeRadioButton(String text) {
        return new JRadioButton(text);
    }

    /**
     * Creates a JRadioButton with the specified text and selection state.
     * @param text The button text
     * @param selected Whether the button is initially selected
     * @return the constructed JRadioButton
     */
    public static JRadioButton makeRadioButton(String text, boolean selected) {
        return new JRadioButton(text, selected);
    }

    /**
     * Creates a ButtonGroup containing the specified radio buttons.
     * @param buttons The radio buttons to add to the group
     * @return the constructed ButtonGroup
     */
    public static ButtonGroup makeButtonGroup(JRadioButton... buttons) {
        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : buttons) {
            group.add(btn);
        }
        return group;
    }

    /**
     * Creates a JCheckBox with the specified text.
     * @param text The checkbox text
     * @return the constructed JCheckBox
     */
    public static JCheckBox makeCheckBox(String text) {
        return new JCheckBox(text);
    }

    /**
     * Creates a JCheckBox with the specified text and selection state.
     * @param text The checkbox text
     * @param selected Whether the checkbox is initially selected
     * @return the constructed JCheckBox
     */
    public static JCheckBox makeCheckBox(String text, boolean selected) {
        return new JCheckBox(text, selected);
    }

    /**
     * Creates a JComboBox with the specified items.
     * @param <T> The type of items in the combo box
     * @param items The items to populate the combo box
     * @return the constructed JComboBox
     */
    @SafeVarargs
    public static <T> JComboBox<T> makeComboBox(T... items) {
        return new JComboBox<>(items);
    }

    /**
     * Creates a JSlider with the specified range.
     * @param min Minimum value
     * @param max Maximum value
     * @param initial Initial value
     * @return the constructed JSlider
     */
    public static JSlider makeSlider(int min, int max, int initial) {
        return new JSlider(min, max, initial);
    }

    /**
     * Creates a JSlider with the specified range and change listener.
     * @param min Minimum value
     * @param max Maximum value
     * @param initial Initial value
     * @param listener Change listener to attach
     * @return the constructed JSlider
     */
    public static JSlider makeSlider(int min, int max, int initial, ChangeListener listener) {
        JSlider slider = new JSlider(min, max, initial);
        slider.addChangeListener(listener);
        return slider;
    }

    /**
     * Creates a fully configured JSlider with the specified range, tick spacing, and change listener.
     * @param min Minimum value
     * @param max Maximum value
     * @param initial Initial value
     * @param majorTick Major tick spacing
     * @param minorTick Minor tick spacing
     * @param listener Change listener to attach
     * @return the constructed JSlider
     */
    public static JSlider makeSlider(int min, int max, int initial, int majorTick, int minorTick, ChangeListener listener) {
        JSlider slider = new JSlider(min, max, initial);
        slider.setMajorTickSpacing(majorTick);
        slider.setMinorTickSpacing(minorTick);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(listener);
        return slider;
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
        menuBar.setBounds(0, 0, window.getWidth(), menuHeight);
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
        fileMenu.add(menuItem("Exit", e -> {
            SoundPlayer.stopBackgroundMusic();
            System.exit(0);
        }));
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
        InputStream is = SwingUtils.class.getResourceAsStream(resourcePath);
        if (is == null) {
            System.err.println("Failed to load image: " + resourcePath);
            return null;
        }
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
