package com.beanshogi.gui.render.piece;

import com.beanshogi.core.pieces.Piece;
import com.beanshogi.core.pieces.normal.*;
import com.beanshogi.core.pieces.normal.slider.*;
import com.beanshogi.core.pieces.promoted.*;
import com.beanshogi.core.pieces.promoted.slider.*;
import com.beanshogi.gui.util.SwingUtils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing piece sprite images.
 */
public class PieceSprites {
    private final Map<Class<? extends Piece>, BufferedImage> sprites = new HashMap<>();

    public PieceSprites() {
        // Unpromoted slider
        load(Bishop.class, "/sprites/pieces/KA.png");
        load(Lance.class, "/sprites/pieces/KY.png");
        load(Rook.class, "/sprites/pieces/HI.png");

        // Unpromoted normal
        load(GoldGeneral.class, "/sprites/pieces/KI.png");
        load(King.class, "/sprites/pieces/OU.png");
        load(Knight.class, "/sprites/pieces/KE.png");
        load(Pawn.class, "/sprites/pieces/FU.png");
        load(SilverGeneral.class, "/sprites/pieces/GI.png");

        // Promoted Slider
        load(PromotedBishop.class, "/sprites/pieces/RY.png");
        load(PromotedRook.class, "/sprites/pieces/UM.png");

        // Promoted normal
        load(PromotedKnight.class, "/sprites/pieces/NK.png");
        load(PromotedLance.class, "/sprites/pieces/NY.png");
        load(PromotedPawn.class, "/sprites/pieces/TO.png");
        load(PromotedSilverGeneral.class, "/sprites/pieces/NY.png");
    }

    /**
     * Generic class for loading piece <-> sprite maps
     * @param pieceClass piece derived class
     * @param resourcePath path of sprite image
     */
    private <T extends Piece> void load(Class<T> pieceClass, String resourcePath) {
        sprites.put(pieceClass, SwingUtils.loadImage(resourcePath));
    }

    /**
     * Get the image based on piece
     * @param pieceClass which piece is queried
     * @return BufferedImage of piece
     */
    public <T extends Piece> BufferedImage get(Class<T> pieceClass) {
        return sprites.get(pieceClass);
    }
}
