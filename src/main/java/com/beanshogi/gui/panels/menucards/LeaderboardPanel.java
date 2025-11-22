package com.beanshogi.gui.panels.menucards;

import java.awt.BorderLayout;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.*;
import com.beanshogi.io.LeaderboardSaveLoad;
import com.beanshogi.leaderboard.Entry;
import com.beanshogi.leaderboard.Leaderboard;
import com.beanshogi.util.PlayerType;

public class LeaderboardPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;

    public LeaderboardPanel(ShogiWindow window) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(18f));
        add(title, BorderLayout.NORTH);

        // --- Table ---
        String[] columnNames = {
            "Winner", "Loser", "Losing Side", "Result", "Moves", "Finished At"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Buttons ---
        JPanel buttons = new JPanel();
        JButton clearButton = SwingUtils.makeButton("Clear Leaderboard", e -> handleClear());
        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));
        buttons.add(clearButton);
        buttons.add(backButton);
        add(buttons, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        Leaderboard leaderboard = LeaderboardSaveLoad.loadLeaderboard();
        tableModel.setRowCount(0);
        List<Entry> entries = new ArrayList<>(leaderboard.getEntries());
        entries.sort(Comparator.comparing((Entry entry) -> {
            Instant instant = entry.getFinishedAtInstant();
            return instant != null ? instant : Instant.EPOCH;
        }).reversed());
        for (Entry entry : entries) {
            Object[] row = new Object[] {
                formatPlayer(entry.getWinnerName(), entry.getWinnerType()),
                formatPlayer(entry.getLoserName(), entry.getLoserType()),
                entry.getLosingSide() != null ? entry.getLosingSide().toString() : "",
                entry.getResultType() != null ? entry.getResultType().name() : "",
                entry.getMovesPlayed(),
                entry.getFinishedAt()
            };
            tableModel.addRow(row);
        }
    }

    private String formatPlayer(String name, PlayerType type) {
        String displayName = name != null ? name : "";
        String typeLabel = formatPlayerType(type);
        if (typeLabel.isEmpty()) {
            return displayName;
        }
        if (displayName.isEmpty()) {
            return typeLabel;
        }
        return displayName + " (" + typeLabel + ")";
    }

    private String formatPlayerType(PlayerType type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case AI:
                return "AI";
            case HUMAN:
                return "Human";
            default:
                return type.name();
        }
    }

    private void handleClear() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Clear all leaderboard records?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            LeaderboardSaveLoad.saveLeaderboard(new Leaderboard());
            refresh();
        }
    }
}
