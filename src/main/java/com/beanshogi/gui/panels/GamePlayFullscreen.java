package com.beanshogi.gui.panels;

import java.awt.BorderLayout;

import javax.swing.*;

import com.beanshogi.game.Controller;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.BackgroundPanel;
import com.beanshogi.gui.util.SwingUtils;

public class GamePlayFullscreen extends BackgroundPanel {

    private static final int CELL_SIZE = 100;

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
        Controller controller = new Controller(sp, urp, alp, hl, pieceLayer, handTop, handBottom, CELL_SIZE,
            () -> window.showCard("MAIN"));

        // Start the game (renders board and kickstarts AI if needed)
        controller.startGame();
    }
}
