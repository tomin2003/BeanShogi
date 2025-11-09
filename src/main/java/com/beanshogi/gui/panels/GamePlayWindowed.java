package com.beanshogi.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.beanshogi.game.Game;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.render.PieceLayerPanel;
import com.beanshogi.gui.render.PieceComponent;
import com.beanshogi.gui.render.PieceSprites;
import com.beanshogi.gui.utils.BackgroundPanel;
import com.beanshogi.gui.utils.SwingUtils;
import com.beanshogi.model.Piece;

public class GamePlayWindowed extends BackgroundPanel {

    private static final int CELL_SIZE = 93;

    public GamePlayWindowed(ShogiWindow window) {
        super("/sprites/gamebg4_3.png");

        setLayout(null); // to position board & buttons freely

        // Back button
        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));
        backButton.setBounds(30, 700, 120, 40);
        add(backButton);

        HighlightLayerPanel panel = new HighlightLayerPanel(); 
        add(panel);
    }
}
