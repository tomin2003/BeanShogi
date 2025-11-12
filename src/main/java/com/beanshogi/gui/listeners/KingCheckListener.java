package com.beanshogi.gui.listeners;

import com.beanshogi.model.Piece;

public interface KingCheckListener {
    void onKingInCheck(boolean inCheck, Piece attackedKing, Piece attackingPiece);
}
