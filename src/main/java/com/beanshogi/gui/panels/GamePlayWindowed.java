package com.beanshogi.gui.panels;

import java.awt.image.BufferedImage;

import javax.swing.*;

import com.beanshogi.game.Game;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.render.PieceComponent;
import com.beanshogi.gui.render.PieceLayerPanel;
import com.beanshogi.gui.render.PieceSprites;
import com.beanshogi.gui.utils.BackgroundPanel;
import com.beanshogi.gui.utils.SwingUtils;
import com.beanshogi.model.Piece;
import com.beanshogi.util.Position;

public class GamePlayWindowed extends JPanel {

    private static final int CELL_SIZE = 90;

    public GamePlayWindowed(ShogiWindow window) {
        //super("/sprites/gamebg4_3.png");

        setLayout(null); // to position board & buttons freely

        // Back button
        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));
        backButton.setBounds(30, 700, 120, 40);
        add(backButton);

        PieceLayerPanel pieceLayer = new PieceLayerPanel();
        pieceLayer.setBounds(54, 55, 904, 1225);
        add(pieceLayer);

        
        // Load sprites + game state
        PieceSprites sprites = new PieceSprites();
        Game game = new Game();
        
        for (Piece piece : game.getBoard().getAllPieces()) {
            BufferedImage pieceImage = sprites.get(piece.getClass());
            if (pieceImage == null) {
                continue;
            }
            PieceComponent comp = new PieceComponent(pieceImage, piece.getSide(), CELL_SIZE);
            pieceLayer.addPiece(comp, piece.getPosition().y, piece.getPosition().x, CELL_SIZE);
        }

        // TEST: highlight features of pieces
        
        // Highlight layer on top
        HighlightLayerPanel highlightLayer = new HighlightLayerPanel();
        highlightLayer.setBounds(pieceLayer.getBounds()); // same as piece layer
        add(highlightLayer); // added after -> paints on top
        
        /*
        for (Position pos : game.getBoard().getPiece(new Position(2,8)).getLegalMoves()) {
            System.out.println(pos.x + "," + pos.y);
            highlightLayer.highlightSquare(pos);
        }
            */
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                highlightLayer.highlightSquare(new Position(i,j));
            }
        }
            
    }
}
