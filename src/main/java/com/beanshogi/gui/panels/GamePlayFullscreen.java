package com.beanshogi.gui.panels;

import java.awt.BorderLayout;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.utils.BackgroundPanel;
import com.beanshogi.gui.utils.SwingUtils;

public class GamePlayFullscreen extends BackgroundPanel {

    private static final int CELL_SIZE = 100;

    public GamePlayFullscreen(ShogiWindow window) {
        super("/sprites/gamebg16_9.png");
        setLayout(null);

        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));
        backButton.setBounds(30, 700, 120, 40);
        add(backButton);
        
        // Create UI layers
        PieceLayerPanel pieceLayer = new PieceLayerPanel();
        pieceLayer.setBounds(460, 80, 1460, 1000);
        add(pieceLayer);

        // Hand pieces panels (captured pieces display)
        // Place to the left of the board in fullscreen layout
        PieceLayerPanel handTop = new PieceLayerPanel();
        handTop.setBounds(20, 80, 400, 200);
        add(handTop);

        PieceLayerPanel handBottom = new PieceLayerPanel();
        handBottom.setBounds(20, 300, 400, 200);
        add(handBottom);
        
        // Create highlight layer
        HighlightLayerPanel hl = new HighlightLayerPanel(CELL_SIZE);
        hl.setBounds(pieceLayer.getBounds());
        add(hl);

        // Create a new game statistics panel - current turn, number of moves
        StatsPanel sp = new StatsPanel(100, 800);
        add(sp);

        // Undo redo move button panel
        UndoRedoPanel urp = new UndoRedoPanel(1000, 600);
        urp.setBounds(1000, 600, 200, 100);
        add(urp, BorderLayout.EAST);

        AttackLinePanel alp = new AttackLinePanel(CELL_SIZE);
        alp.setBounds(0,0,1920,1080);
        add(alp);
        
        // Create controller (which creates the Game internally)
        Controller controller = new Controller(sp, urp, alp, hl, pieceLayer, handTop, handBottom, CELL_SIZE);

        // Ask controller to draw the initial pieces
        controller.renderBoard();
    }
}
