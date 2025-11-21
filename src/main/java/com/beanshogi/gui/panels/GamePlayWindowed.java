package com.beanshogi.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.game.Game;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;
import com.beanshogi.util.Position;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int BOARD_CELL_SIZE = 90;
    private static final int HAND_CELL_SIZE = 70;
    private static final int BOARD_GRID_GAP = 2;
    private static final int HAND_VERT_GAP = 10;

    public GamePlayWindowed(ShogiWindow window, Game game) {
        super("/sprites/gamebg4_3.png");
        setLayout(null);

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
        add(menuBar);

        // PS: These pixel offsets might seem like magic numbers, but they are actually carefully 
        // calculated for the 1280x960 standard 4:3 resolution gameplay BG using GIMP.

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel statsPanel = new StatsPanel(1025, 425);
        add(statsPanel);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(985, 505);
        urp.setBounds(985, 505, 200, 100);
        add(urp, BorderLayout.EAST);

        // Overlay panel for king attack lines
        AttackLinePanel alp = new AttackLinePanel(BOARD_CELL_SIZE, BOARD_GRID_GAP);
        alp.setBounds(42, 67, 870, 895);
        add(alp);
        
        // Board piece overlay panel
        PieceLayerPanel boardPanel = new PieceLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP));
        boardPanel.setBounds(42, 67, 870, 895);
        add(boardPanel);

        // Hand pieces panels, top and bottom on the right of board
        PieceLayerPanel handTop = new PieceLayerPanel(HAND_CELL_SIZE, new Position(0,10));
        handTop.setBounds(910, 50, 1260, 440);
        add(handTop);

        PieceLayerPanel handBottom = new PieceLayerPanel(HAND_CELL_SIZE, new Position(0,10));
        handBottom.setBounds(910, 570, 1260, 910);
        add(handBottom);

        // Create new highlight layer for board
        HighlightLayerPanel boardHighlight = new HighlightLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP), new Color(255,0,0,80));
        boardHighlight.setBounds(boardPanel.getBounds());
        add(boardHighlight);

        // Create highlight layers for hand panels
        HighlightLayerPanel handTopHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, new Position(0,HAND_VERT_GAP), new Color(0,0,255,80));
        handTopHighlight.setBounds(handTop.getBounds());
        add(handTopHighlight);

        HighlightLayerPanel handBottomHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, new Position(0,HAND_VERT_GAP), new Color(0,0,255,80));
        handBottomHighlight.setBounds(handBottom.getBounds());
        add(handBottomHighlight);

        // Create controller
        Controller controller = new Controller(game, this, statsPanel, urp, alp, boardHighlight, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom, () -> window.showCard("MAIN"));

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();
    }
}
