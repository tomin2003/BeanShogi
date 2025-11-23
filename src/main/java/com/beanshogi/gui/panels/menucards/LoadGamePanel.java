package com.beanshogi.gui.panels.menucards;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

import com.beanshogi.core.game.Game;
import com.beanshogi.core.game.Player;
import com.beanshogi.core.board.Board;
import com.beanshogi.core.game.PlayerType;
import com.beanshogi.core.game.Sides;
import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.SwingUtils;
import com.beanshogi.io.GameSaveLoad;

public class LoadGamePanel extends JPanel {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());
    private final ShogiWindow window;
    private final DefaultListModel<File> listModel = new DefaultListModel<>();
    private final JList<File> saveList = new JList<>(listModel);
    private final JTextArea previewArea = new JTextArea();
    private final JButton loadButton;
    private final JButton deleteButton;
    private Runnable backAction;

    public LoadGamePanel(ShogiWindow window) {
        this.window = window;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        backAction = () -> window.showCard("MAIN");

        JLabel title = new JLabel("Load Saved Game", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(18f));
        add(title, BorderLayout.NORTH);

        configureList();
        configurePreview();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildListPane(), buildPreviewPane());
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        loadButton = SwingUtils.makeButton("Load", e -> loadSelectedGame());
        loadButton.setEnabled(false);
        deleteButton = SwingUtils.makeButton("Delete", e -> deleteSelectedSave());
        deleteButton.setEnabled(false);

        JButton backButton = SwingUtils.makeButton("Back", e -> backAction.run());

        JPanel actions = new JPanel();
        actions.add(loadButton);
        actions.add(deleteButton);
        actions.add(backButton);
        add(actions, BorderLayout.SOUTH);
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction != null ? backAction : () -> window.showCard("MAIN");
    }

    /**
     * Auto-refresh saves every time the panel is shown
     */
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshSaveList();
        }
    }

    /**
     * Prepare the list with formatting and adding selection listener
     */
    private void configureList() {
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveList.setVisibleRowCount(12);
        saveList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(String.format("%s", value.getName()));
            label.setOpaque(true);
            label.setBackground(isSelected ? UIManager.getColor("List.selectionBackground") : UIManager.getColor("List.background"));
            label.setForeground(isSelected ? UIManager.getColor("List.selectionForeground") : UIManager.getColor("List.foreground"));
            return label;
        });

        saveList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updatePreview();
        });
    }

    /**
     * Prepare the review pannel with formatting
     */
    private void configurePreview() {
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setOpaque(true);
        previewArea.setBackground(UIManager.getColor("Panel.background"));
        previewArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        previewArea.setText("Select a save to see its details.");
    }

    /**
     * Build the actual JPanel for the list
     * @return list panel
     */
    private JPanel buildListPane() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Saved games:"), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(saveList);
        scrollPane.setPreferredSize(new Dimension(350, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Build the actual JPanel for the preview panel
     * @return preview panel
     */
    private JPanel buildPreviewPane() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Details:"), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(previewArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Add the present saves in folder into the list
     */
    private void refreshSaveList() {
        File dir = new File("savegame");
        File[] files = dir.exists() ? dir.listFiles((d, n) -> n.toLowerCase().endsWith(".json")) : null;

        listModel.clear();
        if (files == null || files.length == 0) {
            previewArea.setText("No saved games found.");
            loadButton.setEnabled(false);
            return;
        }
        
        // Sort the file list by latest
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        for (File file : files) {
            listModel.addElement(file);
        }
        saveList.setSelectedIndex(0);
    }

    /**
     * Add the preview data corresponding to a save
     */
    private void updatePreview() {
        File file = saveList.getSelectedValue();
        if (file == null) {
            previewArea.setText("Select a save to see its details.");
            // The buttons are disabled by default
            loadButton.setEnabled(false);
            deleteButton.setEnabled(false);
            return;
        }
        // Collect a summary of save contents
        try {
            Game game = GameSaveLoad.load(file.getAbsolutePath());
            Board board = game.getBoard();
            Player sente = board.getPlayer(Sides.SENTE);
            Player gote = board.getPlayer(Sides.GOTE);
            String text = String.format(
                "File: %s%nSaved: %s%n%nSente: %s (%s)%nGote: %s (%s)%nMoves played: %d%nPieces on board: %d%nSente hand: %d%nGote hand: %d",
                file.getName(),
                formatTimestamp(file.lastModified()),
                sente != null ? sente.getName() : "?",
                sente != null && sente.getType() == PlayerType.AI ? "AI" : "Human",
                gote != null ? gote.getName() : "?",
                gote != null && gote.getType() == PlayerType.AI ? "AI" : "Human",
                board.moveManager != null ? board.moveManager.getNoOfMoves() : 0,
                board.getAllPieces().size(),
                sente != null ? sente.getHandPieces().size() : 0,
                gote != null ? gote.getHandPieces().size() : 0
            );
            previewArea.setText(text);

            // Enable the buttons when a save is present
            loadButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } catch (RuntimeException ex) {
            previewArea.setText("Failed to read save: " + ex.getMessage());
            loadButton.setEnabled(false);
        }
    }

    /**
     * Perform the loading operations for selected save
     */
    private void loadSelectedGame() {
        File file = saveList.getSelectedValue();
        if (file == null) {
            return;
        }
        try {
            Game game = GameSaveLoad.load(file.getAbsolutePath());
            window.showGamePlay(game);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load save: " + ex.getMessage(),
                    "Load Fail",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Delete the save file from computer
     */
    private void deleteSelectedSave() {
        File file = saveList.getSelectedValue();
        if (file == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this save?\n" + file.getName(),
            "Delete Save",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
            
        if (file.delete()) {
            refreshSaveList();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to delete save: " + file.getName(),
                "Delete Fail",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Converts Unix time stamp into specified format.
     * @param epochMillis millis since 1970.01.01.
     * @return formatted DT string
     */
    private static String formatTimestamp(long epochMillis) {
        return DATE_TIME_FORMAT.format(Instant.ofEpochMilli(epochMillis));
    }
}
