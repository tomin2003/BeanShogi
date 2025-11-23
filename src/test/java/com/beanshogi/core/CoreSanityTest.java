package com.beanshogi.core;

import com.beanshogi.core.board.*;
import com.beanshogi.core.game.*;
import com.beanshogi.core.pieces.*;
import com.beanshogi.core.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoreSanityTest {
    @Test
    void testSidesOpposite() {
        assertEquals(Sides.GOTE, Sides.SENTE.getOpposite());
        assertEquals(Sides.SENTE, Sides.GOTE.getOpposite());
    }

    @Test
    void testPositionBounds() {
        assertTrue(new Position(0,0).inBounds());
        assertFalse(new Position(-1,0).inBounds());
        assertFalse(new Position(9,9).inBounds());
    }

    @Test
    void testPlayerHand() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Piece pawn = new com.beanshogi.core.pieces.normal.Pawn(Sides.SENTE, null, null, null);
        p.addToHand(pawn);
        assertFalse(p.getHandPieces().isEmpty());
        p.removeFromHand(pawn);
        assertTrue(p.getHandPieces().isEmpty());
    }

    @Test
    void testMoveEquality() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Position from = new Position(0,0);
        Position to = new Position(0,1);
        Piece pawn = new com.beanshogi.core.pieces.normal.Pawn(Sides.SENTE, from, null, null);
        Move m1 = new Move(p, from, to, pawn, null, false, false);
        Move m2 = new Move(p, from, to, pawn, null, false, false);
        assertEquals(m1, m2);
    }

    @Test
    void testBoardSetGet() {
        Player p = new Player(Sides.SENTE, "A", PlayerType.HUMAN);
        Board board = new Board(java.util.Collections.singletonList(p));
        Position pos = new Position(4,4);
        Piece king = new com.beanshogi.core.pieces.normal.King(Sides.SENTE, pos, null, board);
        board.setPiece(pos, king);
        assertEquals(king, board.getPiece(pos));
        board.removePiece(pos);
        assertNull(board.getPiece(pos));
    }
}
