package com.beanshogi.io;

import java.io.File;

import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.Leaderboard;

/**
 * Helper for loading and saving leaderboard data.
 */
public class LeaderboardSaveLoad extends Serialize {
    public static Leaderboard loadLeaderboard() {
        File file = new File("leaderboard/leaderboard.json");
        if (!file.exists()) {
            return new Leaderboard();
        }
        try {
            return Serialize.load("leaderboard/leaderboard.json", Leaderboard.class);
        } catch (RuntimeException e) {
            // If file is corrupted, return an empty leaderboard instead of crashing.
            return new Leaderboard();
        }
    }

    public static void saveLeaderboard(Leaderboard leaderboard) {
        Serialize.save(leaderboard, "leaderboard/leaderboard.json");
    }

    public static void appendEntry(Entry entry) {
        Leaderboard leaderboard = loadLeaderboard();
        leaderboard.addEntry(entry);
        saveLeaderboard(leaderboard);
    }
}
