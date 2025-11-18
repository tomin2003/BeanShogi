package com.beanshogi.gui.panels;

import java.awt.BorderLayout;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int CELL_SIZE = 90;

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
        
        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(1000, 600);
        urp.setBounds(1000, 600, 200, 100);
        add(urp, BorderLayout.EAST);

        AttackLinePanel alp = new AttackLinePanel(CELL_SIZE);
        alp.setBounds(42, 67, 870, 895);
        add(alp);
        
        // Create UI layers
        PieceLayerPanel pieceLayer = new PieceLayerPanel();
        pieceLayer.setBounds(42, 67, 870, 895);
        add(pieceLayer);

        // Hand pieces panels (captured pieces display) - placed to the right of the board
        PieceLayerPanel handTop = new PieceLayerPanel();
        handTop.setBounds(915, 50, 1260, 445);
        add(handTop);

        PieceLayerPanel handBottom = new PieceLayerPanel();
        handBottom.setBounds(915, 570, 1260, 915);
        add(handBottom);

        // Create new highlight layer
        HighlightLayerPanel highlightLayer = new HighlightLayerPanel(CELL_SIZE);
        highlightLayer.setBounds(pieceLayer.getBounds());
        add(highlightLayer);

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel statsPanel = new StatsPanel(1000, 445);
        add(statsPanel);

        // Create controller (which creates the Game internally)
        Controller controller = new Controller(statsPanel, urp, alp, highlightLayer, pieceLayer, handTop, handBottom, CELL_SIZE, 
            () -> window.showCard("MAIN"));

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();
    }
}
