package com.beanshogi.leaderboard;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.beanshogi.core.game.PlayerType;
import com.beanshogi.core.game.Sides;

/**
 * Represents a single leaderboard entry.
 */
public class Entry {

        private static final DateTimeFormatter BASE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        private static final DateTimeFormatter DATE_TIME_FORMAT =
            BASE_FORMAT.withZone(ZoneId.systemDefault());

    private String winnerName;
    private String loserName;
    private Sides losingSide;
    private int movesPlayed;
    private ResultType resultType;
    private String finishedAt;
    private PlayerType winnerType;
    private PlayerType loserType;

    public Entry() {
    }

    public Entry(String winnerName, String loserName, Sides losingSide,
                 int movesPlayed, ResultType resultType, Instant finishedAt) {
        this(winnerName, loserName, losingSide, movesPlayed, resultType, finishedAt, null, null);
    }

    public Entry(String winnerName, String loserName, Sides losingSide,
                 int movesPlayed, ResultType resultType, Instant finishedAt,
                 PlayerType winnerType, PlayerType loserType) {
        this.winnerName = winnerName;
        this.loserName = loserName;
        this.losingSide = losingSide;
        this.movesPlayed = movesPlayed;
        this.resultType = resultType;
        this.finishedAt = DATE_TIME_FORMAT.format(finishedAt);
        this.winnerType = winnerType;
        this.loserType = loserType;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getLoserName() {
        return loserName;
    }

    public Sides getLosingSide() {
        return losingSide;
    }

    public int getMovesPlayed() {
        return movesPlayed;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public PlayerType getWinnerType() {
        return winnerType;
    }

    public PlayerType getLoserType() {
        return loserType;
    }

    public Instant getFinishedAtInstant() {
        if (finishedAt == null || finishedAt.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(finishedAt, BASE_FORMAT);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
