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
        this.name = name;
        this.type = type;
        hand = new HandGrid();
    }  

    public Sides getSide() {
        return side;
    }

    public String getName() {
        return name;
    }

    public List<Piece> getHandPieces() {
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

    /**
     * Function for clearing hand contents before starting a new game
     * (in case the hand still has contents left over from previous game)
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Adds a piece to the player's hand. (when captured)
     * @param piece captured piece to be added
     */
    public void addToHand(Piece piece) {
        if (piece == null) {
            return;
        }
        piece.changeSide();
        piece = piece.demote();
        hand.addPiece(piece);
    }    

    /**
     * Remove from hand (when placing down a captured piece) 
     * @param piece captured piece to be removed
     */
    public void removeFromHand(Piece piece) {
        hand.removePiece(piece);
    }
}