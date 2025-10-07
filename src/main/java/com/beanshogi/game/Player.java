package com.beanshogi.game;

import java.util.*;
import com.beanshogi.model.*;
import com.beanshogi.util.*;

public class Player {
    // The color of the player
    private Colors color;
    // The pieces in hand.
    private List<Piece> hand;

    public Player(Colors color) {
        this.color = color;
        hand = new ArrayList<>();
    }  

    public Colors getColor() {
        return color;
    }

    public List<Piece> getHand() {
        return Collections.unmodifiableList(hand);
    }

    // Add to hand (when captured)
    public void addToHand(Piece piece) {
        if (piece == null) return;
        piece.changeColor();
        hand.add(piece);
    }    

    // Remove from hand (when placing down a captured piece)
    public void removeFromHand(Piece piece) {
        hand.remove(piece);
    }
}