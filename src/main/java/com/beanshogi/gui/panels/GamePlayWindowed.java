package com.beanshogi.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;
import com.beanshogi.util.Position;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int BOARD_CELL_SIZE = 90;
    private static final int HAND_CELL_SIZE = 70;
    private static final int BOARD_GRID_GAP = 2;

    public GamePlayWindowed(ShogiWindow window) {
        super("/sprites/gamebg4_3.png");
        setLayout(null);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(SwingUtils.makeMenuItem("New Game", e -> window.showGamePlay()));
        gameMenu.add(SwingUtils.makeMenuItem("Main Menu", e -> window.showCard("MAIN")));
        gameMenu.addSeparator();
        gameMenu.add(SwingUtils.makeMenuItem("Exit", e -> System.exit(0)));

        menuBar.add(gameMenu);
        window.adjustFrameForMenuBar(menuBar);

        // PS: These pixel offsets might seem like magic numbers, but they are actually carefully 
        // calculated for the 1280x960 standard 4:3 resolution gameplay BG using GIMP.

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel statsPanel = new StatsPanel(1025, 425);
        add(statsPanel);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(985, 505);
        urp.setBounds(985, 505, 200, 100);
        add(urp, BorderLayout.EAST);

        AttackLinePanel alp = new AttackLinePanel(BOARD_CELL_SIZE, BOARD_GRID_GAP);
        alp.setBounds(42, 67, 870, 895);
        add(alp);
        
        // Create UI layers
        PieceLayerPanel boardPanel = new PieceLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP));
        boardPanel.setBounds(42, 67, 870, 895);
        add(boardPanel);

        // Hand pieces panels (captured pieces display) - placed to the right of the board
        PieceLayerPanel handTop = new PieceLayerPanel(HAND_CELL_SIZE, new Position(0,8));
        handTop.setBounds(910, 50, 1260, 440);
        add(handTop);

        PieceLayerPanel handBottom = new PieceLayerPanel(HAND_CELL_SIZE, new Position(0,10));
        handBottom.setBounds(910, 570, 1260, 910);
        add(handBottom);

        // Create new highlight layer for board
        HighlightLayerPanel highlightLayer = new HighlightLayerPanel(BOARD_CELL_SIZE, BOARD_GRID_GAP, new Color(255,0,0,80));
        highlightLayer.setBounds(boardPanel.getBounds());
        add(highlightLayer);

        // Create highlight layers for hand panels
        HighlightLayerPanel handTopHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, 0, new Color(0,0,255,80));
        handTopHighlight.setBounds(handTop.getBounds());
        add(handTopHighlight);

        HighlightLayerPanel handBottomHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, 0, new Color(0,0,255,80));
        handBottomHighlight.setBounds(handBottom.getBounds());
        add(handBottomHighlight);

        // Create controller (which creates the Game internally)
        Controller controller = new Controller(statsPanel, urp, alp, highlightLayer, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom, () -> window.showCard("MAIN"));

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();
    }
}
