package com.beanshogi.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.game.Game;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.listeners.ControllerListeners;
import com.beanshogi.gui.panels.overlays.AttackLinePanel;
import com.beanshogi.gui.panels.overlays.HighlightLayerPanel;
import com.beanshogi.gui.panels.overlays.StatsPanel;
import com.beanshogi.gui.panels.overlays.UndoRedoPanel;
import com.beanshogi.gui.panels.overlays.piece.PieceLayerPanel;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;
import com.beanshogi.util.Position;

public class GamePlayFullscreen extends BackgroundPanel {

    private static final int BOARD_CELL_SIZE = 100;
    private static final int HAND_CELL_SIZE = 70;
    private static final int BOARD_GRID_GAP = 2;
    private static final int HAND_VERT_GAP = 10;

    // TODO: make full screen game panel

    public GamePlayFullscreen(ShogiWindow window, Game game) {
        super("/sprites/gamebg16_9.png");
        // Allow for pixel precision alignment of components
        setLayout(null); 

        // Create UI layers
        PieceLayerPanel boardPanel = new PieceLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP));
        boardPanel.setBounds(460, 80, 1460, 1000);
        add(boardPanel);

        // Hand pieces panels (captured pieces display)
        // Place to the left of the board in fullscreen layout
        PieceLayerPanel handTop = new PieceLayerPanel(HAND_CELL_SIZE, new Position(0,5));
        handTop.setBounds(20, 80, 400, 200);
        add(handTop);

        PieceLayerPanel handBottom = new PieceLayerPanel(HAND_CELL_SIZE, new Position(0,5));
        handBottom.setBounds(20, 300, 400, 200);
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

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel sp = new StatsPanel(100, 800);
        add(sp);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(new Position(1000, 600));
        urp.setBounds(1000, 600, 200, 100);
        add(urp, BorderLayout.EAST);

        AttackLinePanel alp = new AttackLinePanel(BOARD_CELL_SIZE, BOARD_GRID_GAP);
        alp.setBounds(0,0,1920,1080);
        add(alp);
        
        ControllerPanels panels = new ControllerPanels(boardHighlight, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom);
        ControllerListeners listeners = new ControllerListeners(sp, urp, alp);

        // Create controller (which creates the Game internally)
        Controller controller = new Controller(game, this, listeners, panels, window::returnToMainMenu);

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();

        // Add menubar with the resign option
        JMenuBar mb = SwingUtils.makeMenuBar(window, game, controller::resign);
        add(mb);
    }
}
