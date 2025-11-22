package com.beanshogi.leaderboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Root leaderboard data object persisted via Serialize.
 */
public class Leaderboard {

    private List<Entry> entries = new ArrayList<>();

    public Leaderboard() {
    }

    public List<Entry> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        return entries;
    }

    public void addEntry(Entry entry) {
        getEntries().add(entry);
    }
}
