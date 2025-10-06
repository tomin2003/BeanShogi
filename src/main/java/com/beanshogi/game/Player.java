package com.beanshogi.game;

import java.util.*;
import com.beanshogi.model.*;
import com.beanshogi.util.*;

public class Player {
    Colors color;
    // The collection of pieces on hand.
    List<Piece> hand;

    // Capture a piece from the opponent and include it in own hand.
    public void capturePiece(Piece piece) {
        piece.setColor(this.color);      
        hand.add(piece);        
    }
}