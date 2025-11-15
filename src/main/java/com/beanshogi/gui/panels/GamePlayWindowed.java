package com.beanshogi.gui.panels;

import java.awt.BorderLayout;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.BackgroundPanel;
import com.beanshogi.gui.utils.SwingUtils;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int CELL_SIZE = 90;

    public GamePlayWindowed(ShogiWindow window) {
        super("/sprites/gamebg4_3.png");
        setLayout(null);

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));
        backButton.setBounds(30, 700, 120, 40);
        add(backButton);
        
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

        // Create new highlight layer
        HighlightLayerPanel highlightLayer = new HighlightLayerPanel(CELL_SIZE);
        highlightLayer.setBounds(pieceLayer.getBounds());
        add(highlightLayer);


        // Create a new game statistics panel - current turn, number of moves
        StatsPanel statsPanel = new StatsPanel(1000, 445);
        add(statsPanel);

        
        // Create controller (which creates the Game internally)
        Controller controller = new Controller(statsPanel, urp, alp, highlightLayer, pieceLayer, CELL_SIZE);

        // Ask controller to draw the initial pieces
        controller.renderBoard();
    }
}
