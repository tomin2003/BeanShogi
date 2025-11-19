package com.beanshogi.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;
import com.beanshogi.util.Position;

public class GamePlayFullscreen extends BackgroundPanel {

    private static final int BOARD_CELL_SIZE = 100;
    private static final int HAND_CELL_SIZE = 70;
    private static final int BOARD_GRID_GAP = 2;
    private static final int HAND_VERT_GAP = 10;


    public GamePlayFullscreen(ShogiWindow window) {
        super("/sprites/gamebg16_9.png");
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

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel sp = new StatsPanel(100, 800);
        add(sp);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(1000, 600);
        urp.setBounds(1000, 600, 200, 100);
        add(urp, BorderLayout.EAST);

        AttackLinePanel alp = new AttackLinePanel(BOARD_CELL_SIZE, BOARD_GRID_GAP);
        alp.setBounds(0,0,1920,1080);
        add(alp);
        
        // Create controller (which creates the Game internally)
        Controller controller = new Controller(this, sp, urp, alp, boardHighlight, handTopHighlight, handBottomHighlight, boardPanel, handTop, handBottom, () -> window.showCard("MAIN"));

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();
    }
}
