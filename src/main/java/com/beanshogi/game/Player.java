package com.beanshogi.game;

import java.util.*;
import com.beanshogi.model.*;
import com.beanshogi.util.*;

/**
 * Class representing a player
 */
public class Player {
    private Sides side;
    private String name;
    private HandGrid hand;
    private PlayerType type;

    public Player(Sides side, String name, PlayerType type) {
        this.side = side;
        this.type = type;
        this.name = name;
        hand = new HandGrid();
    }  

    public Sides getSide() {
        return side;
    }

    public String getName() {
        return name;
    }

    public List<Piece> getHand() {
        return hand.getAllPieces();
    }

    public HandGrid getHandGrid() {
        return hand;
    }

    public boolean isAI() {
        return type == PlayerType.AI;
    }

    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }

    public PlayerType getType() {
        return type;
    }

    // Add to hand (when captured)
    public void addToHand(Piece piece) {
        if (piece == null) {
            return;
        }
        piece.changeSide();
        piece = piece.demote();
        hand.addPiece(piece);
    }    

    // Remove from hand (when placing down a captured piece)
    public void removeFromHand(Piece piece) {
        hand.removePiece(piece);
    }
}