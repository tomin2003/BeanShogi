package com.beanshogi.gui.panels.gameplay;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.core.game.Controller;
import com.beanshogi.core.game.Game;
import com.beanshogi.core.util.Position;
import com.beanshogi.gui.listeners.ControllerListeners;
import com.beanshogi.gui.listeners.defaults.DefaultSoundEventListener;
import com.beanshogi.gui.listeners.defaults.DefaultGameEventListener;
import com.beanshogi.gui.listeners.event.GameEventListener;
import com.beanshogi.gui.listeners.ui.GameOverUIListener;
import com.beanshogi.gui.listeners.event.SoundEventListener;
import com.beanshogi.gui.listeners.ui.UIInteractionListener;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.render.AttackLinePanel;
import com.beanshogi.gui.render.HighlightLayerPanel;
import com.beanshogi.gui.render.piece.PieceLayerPanel;
import com.beanshogi.gui.panels.ControllerPanels;
import com.beanshogi.gui.panels.overlays.StatsPanel;
import com.beanshogi.gui.panels.overlays.UndoRedoPanel;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int BOARD_CELL_SIZE = 90;
    private static final int HAND_CELL_SIZE = 70;
    private static final int BOARD_GRID_GAP = 2;
    private static final int HAND_VERT_GAP = 10;

    public GamePlayWindowed(ShogiWindow window, Game game) {
        super("/gamebg4_3.png");
        
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

        // Create new highlight layer for board move targets (translucent red)
        HighlightLayerPanel boardHighlight = new HighlightLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP), new Color(255,0,0,80));
        boardHighlight.setBounds(boardPanel.getBounds());
        add(boardHighlight);

        // Create highlight layer for currently selected board square (translucent dark red)
        HighlightLayerPanel boardSelectionHighlight = new HighlightLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP), new Color(128,0,0,80));
        boardSelectionHighlight.setBounds(boardPanel.getBounds());
        add(boardSelectionHighlight);

        // Create highlight layers for hand panels (translucent blue highlights)
        HighlightLayerPanel handTopHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, new Position(0,HAND_VERT_GAP), new Color(0,0,255,80));
        handTopHighlight.setBounds(handTop.getBounds());
        add(handTopHighlight);

        HighlightLayerPanel handBottomHighlight = new HighlightLayerPanel(HAND_CELL_SIZE, new Position(0,HAND_VERT_GAP), new Color(0,0,255,80));
        handBottomHighlight.setBounds(handBottom.getBounds());
        add(handBottomHighlight);

        ControllerPanels panels = new ControllerPanels(boardHighlight, boardSelectionHighlight, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom);
        
        // Create listener implementations
        GameEventListener gameEventListener = new DefaultGameEventListener();
        UIInteractionListener uiInteractionListener = new GameOverUIListener(this, window::returnToMainMenu);
        SoundEventListener soundEventListener = new DefaultSoundEventListener();
        
        ControllerListeners listeners = new ControllerListeners(statsPanel, urp, alp, gameEventListener, uiInteractionListener, soundEventListener);

        // Create controller
        Controller controller = new Controller(game, this, listeners, panels);

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();

        // Add menubar with the resign option
        JMenuBar mb = SwingUtils.makeMenuBar(window, game, controller::resign);
        add(mb);
    }
}
