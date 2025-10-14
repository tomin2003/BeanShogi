package com.beanshogi.util;

import java.util.Objects;

public class Position {
    private int x;
    private int y;

    public Position() {
        x = 0;
        y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Check for alignment within 9x9 shogi board bounds
    public boolean inBounds() {
        return x >= 0 && x < 9 && y >= 0 && y < 9;
    }

    @Override
    public boolean equals(Object o) {
        // Check if it's the same object
        if (this == o) {
            return true;
        }
        // Check it it's not of Position type
        if (!(o instanceof Position)) {
            return false;
        }
        // Cast and compare by fields
        Position pos = (Position)o;
        return (this.x == pos.x) && (this.y == pos.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
