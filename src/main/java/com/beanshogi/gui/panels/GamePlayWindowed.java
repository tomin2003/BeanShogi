package com.beanshogi.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.game.Game;
import com.beanshogi.gui.listeners.ControllerListeners;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.panels.overlays.AttackLinePanel;
import com.beanshogi.gui.panels.overlays.HighlightLayerPanel;
import com.beanshogi.gui.panels.overlays.StatsPanel;
import com.beanshogi.gui.panels.overlays.UndoRedoPanel;
import com.beanshogi.gui.panels.overlays.piece.PieceLayerPanel;
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
        
        // Allow for pixel precision alignment of components
        setLayout(null); 

        // PS: These pixel offsets might seem like magic numbers, but they are actually carefully 
        // calculated for the 1280x960 standard 4:3 resolution gameplay BG using GIMP.

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel statsPanel = new StatsPanel(1025, 425);
        add(statsPanel);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(new Position(985, 505));
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

        // Create new highlight layer for board (translucent red highlights)
        HighlightLayerPanel boardHighlight = new HighlightLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP), new Color(255,0,0,80));
        boardHighlight.setBounds(boardPanel.getBounds());
        add(boardHighlight);

        // Create highlight layers for hand panels (translucent blue highlights)
        HighlightLayerPanel handTopHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, new Position(0,HAND_VERT_GAP), new Color(0,0,255,80));
        handTopHighlight.setBounds(handTop.getBounds());
        add(handTopHighlight);

        HighlightLayerPanel handBottomHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, new Position(0,HAND_VERT_GAP), new Color(0,0,255,80));
        handBottomHighlight.setBounds(handBottom.getBounds());
        add(handBottomHighlight);

        ControllerPanels panels = new ControllerPanels(boardHighlight, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom);
        ControllerListeners listeners = new ControllerListeners(statsPanel, urp, alp);

        // Create controller
        Controller controller = new Controller(game, this, listeners, panels, window::returnToMainMenu);

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();

        // Add menubar with the resign option
            JMenuBar mb = SwingUtils.makeMenuBar(window, game, controller::resign);
        add(mb);
    }
}
