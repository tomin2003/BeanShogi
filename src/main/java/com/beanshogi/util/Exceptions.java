package com.beanshogi.util;

public class Exceptions {
    public static class PieceNotFoundException extends RuntimeException {
        public PieceNotFoundException(String message) {
            super(message);
        }
    }

    public static class PlayerNotFoundException extends RuntimeException {
        public PlayerNotFoundException(String message) {
            super(message);
        }
    }
}
