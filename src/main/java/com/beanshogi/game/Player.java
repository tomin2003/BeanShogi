package com.beanshogi.game;

import java.util.*;
import com.beanshogi.model.*;
import com.beanshogi.util.*;

/**
 * Class representing a player
 */
public class Player {
    private Sides side;
    private List<Piece> hand;
    private PlayerType type;

    public Player(Sides side, PlayerType type) {
        this.side = side;
        this.type = type;
        hand = new ArrayList<>();
    }  

    public Sides getSide() {
        return side;
    }

    public List<Piece> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public PlayerType getType() {
        return type;
    }

    public boolean isAI() {
        return type == PlayerType.AI;
    }

    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }

    // Add to hand (when captured)
    public void addToHand(Piece piece) {
        if (piece == null) {
            return;
        }
        piece.changeSide();
        piece = piece.demote();
        hand.add(piece);
    }    

    // Remove from hand (when placing down a captured piece)
    public void removeFromHand(Piece piece) {
        hand.remove(piece);
    }
}