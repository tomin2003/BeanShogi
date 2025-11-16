    package com.beanshogi.model;

    import com.beanshogi.game.*;
    import com.beanshogi.util.Position;

    /**
     * Represents an act of movement, immutable
     * @param player the player who initiated the move
     * @param from the square from which the move was made
     * @param to the square to which the move is made
     * @param movedPiece the piece that was moved
     * @param capturedPiece the piece that was caputured as a result (null if didn't capture any)
     * @param isPromotion flag, true when the move promoted the piece
     */
    public class Move {
        private final Player player;
        private final Position from;
        private final Position to;
        private final Piece movedPiece;
        private final Piece capturedPiece;
        private final boolean isPromotion;

        public Move(Player player, Position from, Position to, Piece movedPiece, Piece capturedPiece, boolean isPromotion) {
            this.player = player;
            this.from = from;
            this.to = to;
            this.movedPiece = movedPiece;
            this.capturedPiece = capturedPiece;
            this.isPromotion = isPromotion;
        }

        public Player getPlayer() { 
            return player;
        }

        public Position getFrom() { 
            return from;
        }

        public Position getTo() { 
            return to; 
        }

        public Piece getMovedPiece() { 
            return movedPiece;
        }

        public Piece getCapturedPiece() { 
            return capturedPiece;
        }
        
        public boolean isPromotion() { 
            return isPromotion; 
        }

        /**
         * Check if this move is a hand drop (piece from captured pieces to board).
         * Hand drops are identified by from == to.
         * @return true if this is a hand drop move
         */
        public boolean isHandDrop() {
            return from != null && to != null && from.equals(to);
        }
    }