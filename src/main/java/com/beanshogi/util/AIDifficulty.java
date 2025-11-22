package com.beanshogi.util;

public enum AIDifficulty {
    EASY("Easy", 1, 160),
    NORMAL("Normal", 2, 60),
    HARD("Hard", 3, 0);

    private final String displayName;
    private final int searchDepth;
    private final int orderingNoise;

    AIDifficulty(String displayName, int searchDepth, int orderingNoise) {
        this.displayName = displayName;
        this.searchDepth = searchDepth;
        this.orderingNoise = orderingNoise;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public int getOrderingNoise() {
        return orderingNoise;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
