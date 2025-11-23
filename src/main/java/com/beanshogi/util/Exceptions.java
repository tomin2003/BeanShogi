package com.beanshogi.util;

/**
 * Container for custom runtime exceptions used throughout the application.
 */
public class Exceptions {
    /**
     * Thrown when a piece cannot be found at an expected location.
     */
    public static class PieceNotFoundException extends RuntimeException {
        public PieceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when a player cannot be found for a given side.
     */
    public static class PlayerNotFoundException extends RuntimeException {
        public PlayerNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when attempting to add a piece to a full hand grid.
     */
    public static class HandFullException extends RuntimeException {
        public HandFullException(String message) {
            super(message);
        }
    }
}
