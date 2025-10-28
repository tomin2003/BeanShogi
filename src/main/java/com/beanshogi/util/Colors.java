package com.beanshogi.util;

// Enumeration for piece colors.
public enum Colors {
    BLACK(1),
    WHITE(-1);

    private final int alignFactor;
    
    Colors(int alignFactor) { 
        this.alignFactor = alignFactor; 
    }

    public int getAlignFactor() { 
        return alignFactor; 
    }

    public Colors getInverse() {
        Colors color = this;
        if (color == Colors.BLACK) {
            color = Colors.WHITE;
        } else {
            color = Colors.BLACK;
        }
        return color;
    }
}