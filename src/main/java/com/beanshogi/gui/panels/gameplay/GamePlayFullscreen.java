package com.beanshogi.gui.panels.gameplay;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.core.game.Controller;
import com.beanshogi.core.game.Game;
import com.beanshogi.core.util.Position;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.listeners.ControllerListeners;
import com.beanshogi.gui.listeners.defaults.DefaultGameEventListener;
import com.beanshogi.gui.listeners.defaults.DefaultSoundEventListener;
import com.beanshogi.gui.listeners.event.GameEventListener;
import com.beanshogi.gui.listeners.event.SoundEventListener;
import com.beanshogi.gui.listeners.ui.GameOverUIListener;
import com.beanshogi.gui.panels.ControllerPanels;
import com.beanshogi.gui.panels.overlays.StatsPanel;
import com.beanshogi.gui.panels.overlays.UndoRedoPanel;
import com.beanshogi.gui.render.AttackLinePanel;
import com.beanshogi.gui.render.HighlightLayerPanel;
import com.beanshogi.gui.render.piece.PieceLayerPanel;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;

public class GamePlayFullscreen extends BackgroundPanel {

    private static final int BOARD_CELL_SIZE = 100;
    private static final int HAND_CELL_SIZE = 70;
    private static final int BOARD_GRID_GAP = 2;
    private static final int HAND_VERT_GAP = 10;

    // TODO: make full screen game panel

    public GamePlayFullscreen(ShogiWindow window, Game game) {
        super("/gamebg16_9.png");
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
        
        // Create highlight layer for board move targets (translucent red)
        HighlightLayerPanel boardHighlight = new HighlightLayerPanel(BOARD_CELL_SIZE, new Position(BOARD_GRID_GAP), new Color(255,0,0,80));
        boardHighlight.setBounds(boardPanel.getBounds());
        add(boardHighlight);

        // Create highlight layer for selected board square (translucent dark red)
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
        
        ControllerPanels panels = new ControllerPanels(boardHighlight, boardSelectionHighlight, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom);
        
        // Create listener implementations
        GameEventListener gameEventListener = new DefaultGameEventListener();
        GameOverUIListener uiInteractionListener = new GameOverUIListener(this, window::returnToMainMenu);
        SoundEventListener soundEventListener = new DefaultSoundEventListener();

        ControllerListeners listeners = new ControllerListeners(sp, urp, alp, gameEventListener, uiInteractionListener, soundEventListener);

        // Create controller (which creates the Game internally)
        Controller controller = new Controller(game, this, listeners, panels);

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();

        // Add menubar with the resign option
        JMenuBar mb = SwingUtils.makeMenuBar(window, game, controller::resign);
        add(mb);
    }
}
