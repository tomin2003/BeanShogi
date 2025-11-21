package com.beanshogi.util;

public class Exceptions {
    public static class PieceNotFoundException extends RuntimeException {
        public PieceNotFoundException(String message) {
            super(message);
        }
    }
    
}
