package com.beanshogi.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.listeners.GameStatsListener;
import com.beanshogi.gui.piece.PieceLayerPanel;
import com.beanshogi.gui.utils.BackgroundPanel;
import com.beanshogi.gui.utils.FilledRectanglePanel;
import com.beanshogi.gui.utils.SwingUtils;
import com.beanshogi.util.Sides;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int CELL_SIZE = 90;

    public GamePlayWindowed(ShogiWindow window) {
        super("/sprites/gamebg4_3.png");
        setLayout(null);

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));
        backButton.setBounds(30, 700, 120, 40);
        add(backButton);
        
        // Create a new game statistics panel - current turn, number of moves
        StatsPanel statsPanel = new StatsPanel(1000, 445);
        add(statsPanel);

        // Create UI layers
        PieceLayerPanel pieceLayer = new PieceLayerPanel();
        pieceLayer.setBounds(50, 50, 900, 1200);
        add(pieceLayer);

        HighlightLayerPanel highlightLayer = new HighlightLayerPanel(CELL_SIZE);
        highlightLayer.setBounds(pieceLayer.getBounds());
        add(highlightLayer);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(1000, 600);
        urp.setBounds(1000, 600, 200, 100);
        add(urp, BorderLayout.EAST);

        AttackLinePanel alp = new AttackLinePanel(CELL_SIZE);
        alp.setBounds(50, 50, 900, 1200);
        add(alp);
        
        // Create controller (which creates the Game internally)
        Controller controller = new Controller(statsPanel, urp, alp, highlightLayer, pieceLayer, CELL_SIZE);

        urp.setController(controller);


        // Ask controller to draw the initial pieces
        controller.renderBoard();
    }
}
